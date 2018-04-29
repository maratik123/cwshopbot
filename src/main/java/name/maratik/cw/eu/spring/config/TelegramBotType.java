package name.maratik.cw.eu.spring.config;

import name.maratik.cw.eu.spring.LongPollingTelegramBotService;
import name.maratik.cw.eu.spring.TelegramBotService;
import name.maratik.cw.eu.spring.WebhookTelegramBotService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.telegram.telegrambots.TelegramBotsApi;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum TelegramBotType {
    LONG_POLLING {
        @Override
        public TelegramBotService createService(TelegramBotBuilder botBuilder, TelegramBotsApi api, ConfigurableBeanFactory beanFactory) {
            return new LongPollingTelegramBotService(botBuilder, api, beanFactory);
        }
    },
    WEBHOOK {
        @Override
        public TelegramBotService createService(TelegramBotBuilder botBuilder, TelegramBotsApi api, ConfigurableBeanFactory beanFactory) {
            return new WebhookTelegramBotService(botBuilder, api, beanFactory);
        }
    };

    public abstract TelegramBotService createService(TelegramBotBuilder botBuilder, TelegramBotsApi api, ConfigurableBeanFactory beanFactory);
}
