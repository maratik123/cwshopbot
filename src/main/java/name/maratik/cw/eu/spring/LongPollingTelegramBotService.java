package name.maratik.cw.eu.spring;

import name.maratik.cw.eu.spring.config.TelegramBotBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class LongPollingTelegramBotService extends TelegramBotService implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(LongPollingTelegramBotService.class);

    private final String username;
    private final String token;
    private final ExecutorService botExecutor;

    public LongPollingTelegramBotService(TelegramBotBuilder botBuilder, TelegramBotsApi api, ConfigurableBeanFactory beanFactory) {
        super(botBuilder, api, beanFactory);
        username = botBuilder.getUsername();
        token = botBuilder.getToken();
        botExecutor = Executors.newFixedThreadPool(botBuilder.getMaxThreads());
    }

    @Override
    protected DefaultAbsSender createAndRegisterClient(TelegramBotsApi api) throws TelegramApiRequestException {
        TelegramLongPollingBot longPollingClient = new TelegramBotLongPollingImpl();
        api.registerBot(longPollingClient);
        return longPollingClient;
    }

    @Override
    public void close() {
        botExecutor.shutdown();
        try {
            if (!botExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.error("Bot executor did not terminated in 5 seconds");
                int droppedTasks = botExecutor.shutdownNow().size();
                logger.error("Executor was abruptly shut down. {} tasks will not be executed", droppedTasks);
            }
        } catch (InterruptedException e) {
            logger.error("Bot executor service termination awaiting failed", e);
        }
    }

    private class TelegramBotLongPollingImpl extends TelegramLongPollingBot {
        @Override
        public void onUpdateReceived(Update update) {
            CompletableFuture.runAsync(() ->
                updateProcess(update).ifPresent(result -> {
                    try {
                        getClient().execute(result);
                        logger.debug("Update: {}. Message: {}. Successfully sent", update, result);
                    } catch (TelegramApiException e) {
                        logger.error("Update: {}. Can not send message {} to telegram: ", update, result, e);
                    }
                }), botExecutor);
        }

        @Override
        public String getBotUsername() {
            return username;
        }

        @Override
        public String getBotToken() {
            return token;
        }
    }
}
