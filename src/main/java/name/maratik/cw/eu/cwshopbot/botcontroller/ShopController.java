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
package name.maratik.cw.eu.cwshopbot.botcontroller;

import name.maratik.cw.eu.spring.annotation.TelegramBot;
import name.maratik.cw.eu.spring.annotation.TelegramCommand;
import name.maratik.cw.eu.spring.annotation.TelegramForward;
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
    public SendMessage message(long userId, User user, String message) {
        return new SendMessage()
            .setChatId(userId)
            .setText(String.format("Hi %s! You've sent me message: \"%s\"",
                user.getFirstName(),
                message
            ));
    }

    @TelegramCommand(commands = "/text", description = "This is a test method")
    public SendMessage test(long userId, String message, User user) {
        return processMessage(userId, message, user);
    }

    private static SendMessage processMessage(long userId, String message, User user) {
        logger.info("Incoming message from: {}, data: {}", userId, message);

        return new SendMessage()
            .setChatId(userId)
            .setText(String.format("Hi %s! You've sent me command with argument: \"%s\"",
                user.getFirstName(),
                message
            ));
    }

    @TelegramCommand(commands = "/hiddenCommand", description = "This is a hidden test method", hidden = true)
    public SendMessage testHidden(long userId, User user, String message) {
        return processMessage(userId, message, user);
    }

    @TelegramForward("${cwuserid}")
    public SendMessage forward(String message, User user, long userId) {
        logger.info("Accepted incoming forward data: {}", message);

        return new SendMessage()
            .setChatId(userId)
            .setText(String.format("Hi %s! You've forwarded me message: %s",
                user.getFirstName(),
                message
            ));
    }

    @TelegramForward
    public SendMessage defaultForward(long userId, User user) {
        logger.info("Accepted unsupported forward data from user: ", userId);
        return new SendMessage()
            .setChatId(userId)
            .setText(String.format("Hi %s, I can't recognize you!",
                user.getFirstName()
            ));
    }
}
