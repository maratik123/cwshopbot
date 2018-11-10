//    cwshopbot
//    Copyright (C) 2018  Marat Bukharov.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Affero General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Affero General Public License for more details.
//
//    You should have received a copy of the GNU Affero General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package name.maratik.cw.cwshopbot.application.config;

import name.maratik.spring.telegram.TelegramBotService;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Configuration
@EnableRabbit
public class MocksConfig {
    @Bean
    public Map<Object, Consumer<Object>> mocks() {
        return new IdentityHashMap<>();
    }

    @Bean
    public TelegramBotService telegramBotService() {
        DefaultAbsSender client = resetAndAddToMocks(DefaultAbsSender.class, mock -> {});
        return resetAndAddToMocks(TelegramBotService.class, mock -> resetTelegramBotService(mock, client));
    }

    @SuppressWarnings("unchecked")
    private <T> T resetAndAddToMocks(Class<T> mockedClass, Consumer<? super T> resetAction) {
        T mock = mock(mockedClass, RETURNS_SMART_NULLS);
        resetAction.accept(mock);
        mocks().put(mock, (Consumer<Object>) resetAction);
        return mock;
    }

    private static void resetTelegramBotService(TelegramBotService telegramBotService, DefaultAbsSender client) {
        given(telegramBotService.getClient()).willReturn(client);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        return resetAndAddToMocks(TelegramBotsApi.class, mock -> {});
    }

    @Bean
    public TestRabbitTemplate template() {
        return new TestRabbitTemplate(connectionFactory());
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        Channel channel = resetAndAddToMocks(Channel.class, MocksConfig::resetChannel);
        Connection connection = resetAndAddToMocks(Connection.class, mock -> resetConnection(mock, channel));
        return resetAndAddToMocks(ConnectionFactory.class, mock -> resetFactory(mock, connection));
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }

    private static void resetChannel(Channel channel) {
        given(channel.isOpen()).willReturn(true);
    }

    private static void resetConnection(Connection connection, Channel channel) {
        given(connection.createChannel(anyBoolean())).willReturn(channel);
    }

    private static void resetFactory(ConnectionFactory factory, Connection connection) {
        given(factory.createConnection()).willReturn(connection);
    }
}
