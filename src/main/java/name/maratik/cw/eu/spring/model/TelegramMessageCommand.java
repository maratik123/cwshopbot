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
package name.maratik.cw.eu.spring.model;

import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class TelegramMessageCommand {
    private final String command;
    private final String argument;
    private final boolean isCommand;

    public TelegramMessageCommand(String command, String argument) {
        this.command = command;
        this.argument = argument;
        isCommand = isSlashStart(command);
    }

    public TelegramMessageCommand(String message) {
        if (isSlashStart(message)) {
            int spacePos = message.indexOf(' ');
            if (spacePos != -1) {
                command = message.substring(0, spacePos);
                argument = message.substring(spacePos + 1);
            } else {
                command = message;
                argument = null;
            }
            isCommand = true;
        } else {
            command = null;
            argument = message;
            isCommand = false;
        }
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

    @Override
    public String toString() {
        return "TelegramMessageCommand{" +
            "command='" + command + '\'' +
            ", argument='" + argument + '\'' +
            ", isCommand=" + isCommand +
            '}';
    }

    private static boolean isSlashStart(String message) {
        return message != null && message.startsWith("/");
    }
}
