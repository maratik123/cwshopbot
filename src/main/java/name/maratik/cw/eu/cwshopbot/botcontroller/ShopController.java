package name.maratik.cw.eu.cwshopbot.botcontroller;

import name.maratik.cw.eu.spring.annotation.TelegramBot;
import name.maratik.cw.eu.spring.annotation.TelegramCommand;
import name.maratik.cw.eu.spring.annotation.TelegramMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.User;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@TelegramBot
public class ShopController {
    private static final Logger logger = LogManager.getLogger(ShopController.class);

    @TelegramMessage
    public SendMessage message(User user, String message) {
        return new SendMessage()
            .setChatId(user.getId().longValue())
            .setText(String.format("Hi %s! You've sent me message: \"%s\"",
                user.getFirstName(),
                message
            ));
    }

    @TelegramCommand(value = "/text", description = "This is a test method")
    public SendMessage test(User user, String message) {
        return processMessage(user, message);
    }

    private static SendMessage processMessage(User user, String message) {
        logger.info("Incoming message from: {}, data: {}", user::getId, () -> message);

        return new SendMessage()
            .setChatId(user.getId().longValue())
            .setText(String.format("Hi %s! You've sent me command with argument: \"%s\"",
                user.getFirstName(),
                message
            ));
    }

    @TelegramCommand(value = "/hiddenCommand", description = "This is a hidden test method", hidden = true)
    public SendMessage testHidden(User user, String message) {
        return processMessage(user, message);
    }
}
