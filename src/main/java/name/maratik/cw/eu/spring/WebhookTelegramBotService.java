package name.maratik.cw.eu.spring;

import name.maratik.cw.eu.spring.config.TelegramBotBuilder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class WebhookTelegramBotService extends TelegramBotService {
    private final String username;
    private final String token;
    private final String path;

    public WebhookTelegramBotService(TelegramBotBuilder botBuilder, TelegramBotsApi api, ConfigurableBeanFactory beanFactory) {
        super(api, beanFactory);
        username = botBuilder.getUsername();
        token = botBuilder.getToken();
        path = botBuilder.getPath();

    }

    @Override
    protected DefaultAbsSender createAndRegisterClient(TelegramBotsApi api) throws TelegramApiRequestException {
        TelegramWebhookBot webhookClient = new TelegramBotWebhookImpl();
        api.registerBot(webhookClient);
        return webhookClient;
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
