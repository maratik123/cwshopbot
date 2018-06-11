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
package name.maratik.cw.eu.spring.config;

import name.maratik.cw.eu.spring.LongPollingTelegramBotService;
import name.maratik.cw.eu.spring.TelegramBotService;
import name.maratik.cw.eu.spring.WebhookTelegramBotService;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.telegram.telegrambots.TelegramBotsApi;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum TelegramBotType {
    LONG_POLLING {
        @Override
        public TelegramBotService createService(
            TelegramBotBuilder botBuilder, TelegramBotsApi api, EmbeddedValueResolver embeddedValueResolver
        ) {
            return new LongPollingTelegramBotService(botBuilder, api, embeddedValueResolver);
        }
    },
    WEBHOOK {
        @Override
        public TelegramBotService createService(
            TelegramBotBuilder botBuilder, TelegramBotsApi api, EmbeddedValueResolver embeddedValueResolver
        ) {
            return new WebhookTelegramBotService(botBuilder, api, embeddedValueResolver);
        }
    };

    public abstract TelegramBotService createService(
        TelegramBotBuilder botBuilder, TelegramBotsApi api, EmbeddedValueResolver embeddedValueResolver
    );
}
