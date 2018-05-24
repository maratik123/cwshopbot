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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.bots.DefaultAbsSender;

import java.util.IdentityHashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Configuration
public class MocksConfig {
    @Bean
    public Map<Object, Runnable> mocks() {
        return new IdentityHashMap<>();
    }

    @Bean
    public TelegramBotService telegramBotService(Map<Object, Runnable> mocks) {
        DefaultAbsSender client = mock(DefaultAbsSender.class, RETURNS_SMART_NULLS);
        mocks.put(client, () -> {});
        TelegramBotService telegramBotService = mock(TelegramBotService.class, RETURNS_SMART_NULLS);
        resetTelegramBotService(telegramBotService, client);
        mocks.put(telegramBotService, () -> resetTelegramBotService(telegramBotService, client));
        return telegramBotService;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(Map<Object, Runnable> mocks) {
        TelegramBotsApi telegramBotsApi = mock(TelegramBotsApi.class, RETURNS_SMART_NULLS);
        mocks.put(telegramBotsApi, () -> {});
        return mock(TelegramBotsApi.class, RETURNS_SMART_NULLS);
    }

    private static void resetTelegramBotService(TelegramBotService telegramBotService, DefaultAbsSender client) {
        given(telegramBotService.getClient()).willReturn(client);
    }
}
