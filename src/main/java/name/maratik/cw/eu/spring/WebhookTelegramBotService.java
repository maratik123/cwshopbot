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

import name.maratik.cw.eu.spring.config.TelegramBotBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class WebhookTelegramBotService extends TelegramBotService {
    private static final Logger logger = LogManager.getLogger(WebhookTelegramBotService.class);

    private final String username;
    private final String token;
    private final String path;
    private final TelegramWebhookBot client;

    public WebhookTelegramBotService(TelegramBotBuilder botBuilder, TelegramBotsApi api, ConfigurableBeanFactory beanFactory) {
        super(api, beanFactory);
        username = botBuilder.getUsername();
        token = botBuilder.getToken();
        path = botBuilder.getPath();
        client = new TelegramBotWebhookImpl();
        try {
            api.registerBot(client);
        } catch (TelegramApiRequestException e) {
            logger.error("Can not register Long Polling with {}", botBuilder, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public TelegramWebhookBot getClient() {
        return client;
    }

    private class TelegramBotWebhookImpl extends TelegramWebhookBot {

        @Override
        public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
            return updateProcess(update).orElse(null);
        }

        @Override
        public String getBotUsername() {
            return username;
        }

        @Override
        public String getBotToken() {
            return token;
        }

        @Override
        public String getBotPath() {
            return path;
        }
    }
}
