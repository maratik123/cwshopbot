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
package name.maratik.cw.eu.spring.config;

import name.maratik.cw.eu.spring.TelegramBeanPostProcessor;
import name.maratik.cw.eu.spring.TelegramBotService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class TelegramBotConfiguration implements ImportAware {
    @Override
    public void setImportMetadata(@NonNull AnnotationMetadata importMetadata) {
    }

    @Bean
    public TelegramBeanPostProcessor telegramBeanPostProcessor(TelegramBotService telegramBotService) {
        return new TelegramBeanPostProcessor(telegramBotService);
    }

    @Bean
    public TelegramBotService telegramBotService(TelegramBotType telegramBotType, TelegramBotBuilder telegramBotBuilder, TelegramBotsApi api, ConfigurableBeanFactory beanFactory) {
        return telegramBotType.createService(telegramBotBuilder, api, beanFactory);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        ApiContextInitializer.init();
        return new TelegramBotsApi();
    }
}
