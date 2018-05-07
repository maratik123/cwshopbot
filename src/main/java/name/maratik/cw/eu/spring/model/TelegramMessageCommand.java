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
package name.maratik.cw.eu.spring.model;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class TelegramMessageCommand {
    private final String command;
    private final String argument;
    private final boolean isCommand;
    private final Long forwardedFrom;

    public TelegramMessageCommand(Update update) {
        Message message = update.getMessage();
        String messageText = message.getText();
        if (isSlashStart(messageText)) {
            int spacePos = messageText.indexOf(' ');
            if (spacePos != -1) {
                command = messageText.substring(0, spacePos);
                argument = messageText.substring(spacePos + 1);
            } else {
                command = messageText;
                argument = null;
            }
            isCommand = true;
        } else {
            command = null;
            argument = messageText;
            isCommand = false;
        }
        this.forwardedFrom = Optional.ofNullable(message.getForwardFrom())
            .map(User::getId)
            .map(Integer::longValue)
            .orElse(null);
    }

    public Optional<String> getCommand() {
        return Optional.ofNullable(command);
    }

    public Optional<String> getArgument() {
        return Optional.ofNullable(argument);
    }

    public boolean isCommand() {
        return isCommand;
    }

    public Optional<Long> getForwardedFrom() {
        return Optional.ofNullable(forwardedFrom);
    }

    @Override
    public String toString() {
        return "TelegramMessageCommand{" +
            "command='" + command + '\'' +
            ", argument='" + argument + '\'' +
            ", isCommand=" + isCommand +
            ", forwardedFrom=" + forwardedFrom +
            '}';
    }

    private static boolean isSlashStart(String message) {
        return message != null && message.startsWith("/");
    }
}
