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
package name.maratik.cw.eu.cwshopbot.application.config;

import name.maratik.cw.eu.spring.TelegramBotService;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.DefaultAbsSender;

import java.util.IdentityHashMap;
import java.util.Map;

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
    public Map<Object, Runnable> mocks() {
        return new IdentityHashMap<>();
    }

    @Bean
    public TelegramBotService telegramBotService() {
        DefaultAbsSender client = mock(DefaultAbsSender.class, RETURNS_SMART_NULLS);
        mocks().put(client, () -> {});
        TelegramBotService telegramBotService = mock(TelegramBotService.class, RETURNS_SMART_NULLS);
        resetTelegramBotService(telegramBotService, client);
        mocks().put(telegramBotService, () -> resetTelegramBotService(telegramBotService, client));
        return telegramBotService;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() {
        TelegramBotsApi telegramBotsApi = mock(TelegramBotsApi.class, RETURNS_SMART_NULLS);
        mocks().put(telegramBotsApi, () -> {});
        return mock(TelegramBotsApi.class, RETURNS_SMART_NULLS);
    }

    @Bean
    public TestRabbitTemplate template() {
        return new TestRabbitTemplate(connectionFactory());
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory factory = mock(ConnectionFactory.class);
        Connection connection = mock(Connection.class);
        Channel channel = mock(Channel.class);
        resetFactory(factory, connection);
        resetConnection(connection, channel);
        resetChannel(channel);
        mocks().put(factory, () -> resetFactory(factory, connection));
        mocks().put(connection, () -> resetConnection(connection, channel));
        mocks().put(channel, () -> resetChannel(channel));
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }

    private static void resetTelegramBotService(TelegramBotService telegramBotService, DefaultAbsSender client) {
        given(telegramBotService.getClient()).willReturn(client);
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
