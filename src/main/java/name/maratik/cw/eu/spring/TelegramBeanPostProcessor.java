//    cwshopbot
//    Copyright (C) 2018  Marat Bukharov.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package name.maratik.cw.eu.spring;

import name.maratik.cw.eu.spring.annotation.TelegramBot;
import name.maratik.cw.eu.spring.annotation.TelegramCommand;
import name.maratik.cw.eu.spring.annotation.TelegramForward;
import name.maratik.cw.eu.spring.annotation.TelegramMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;

import javax.annotation.Priority;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Priority(10)
public class TelegramBeanPostProcessor implements BeanPostProcessor {
    private static final Logger logger = LogManager.getLogger(TelegramBeanPostProcessor.class);

    private final TelegramBotService telegramBotService;
    private final Map<String, Class<?>> botControllerMap = new HashMap<>();

    public TelegramBeanPostProcessor(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (AnnotationUtils.findAnnotation(beanClass, TelegramBot.class) != null) {
            logger.info("Init TelegramBot controller: {}", beanClass);
            botControllerMap.put(beanName, beanClass);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, String beanName) throws BeansException {
        Class<?> original = botControllerMap.get(beanName);
        if (original != null) {
            logger.info("Processing class {} as bean", bean::getClass,  () -> beanName);
            for (Method method : original.getDeclaredMethods()) {
                logger.info("Found method {}", method::getName);
                if (AnnotatedElementUtils.hasAnnotation(method, TelegramCommand.class)) {
                    bindCommandController(bean, method);
                }
                if (AnnotatedElementUtils.hasAnnotation(method, TelegramMessage.class)) {
                    bindMessageController(bean, method);
                }
                if (AnnotatedElementUtils.hasAnnotation(method, TelegramForward.class)) {
                    bindForwardController(bean, method);
                }
            }
        }
        return bean;
    }

    private void bindMessageController(Object bean, Method method) {
        logger.info("Init TelegramBot message controller: {}:{}", bean::getClass, method::getName);
        telegramBotService.addDefaultMessageHandler(bean, method);
    }

    private void bindCommandController(Object bean, Method method) {
        logger.info("Init TelegramBot command controller: {}:{}", bean::getClass, method::getName);
        telegramBotService.addHandler(bean, method);
    }

    private void bindForwardController(Object bean, Method method) {
        logger.info("Init TelegramBot forward controller: {}:{}", bean::getClass, method::getName);
        telegramBotService.addForwardMessageHandler(bean, method);
    }
}
