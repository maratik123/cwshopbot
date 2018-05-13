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
package name.maratik.cw.eu.cwshopbot.model;

import org.telegram.telegrambots.api.objects.Message;

import java.time.Instant;
import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ForwardKey {
    private final Instant timestamp;
    private final String message;

    private ForwardKey(Instant timestamp, String message) {
        this.timestamp = Objects.requireNonNull(timestamp);
        this.message = Objects.requireNonNull(message);
    }

    public ForwardKey(Message message) {
        this(Instant.ofEpochSecond(message.getForwardDate()), message.getText());
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ForwardKey)) {
            return false;
        }

        ForwardKey that = (ForwardKey) o;

        if (!getTimestamp().equals(that.getTimestamp())) {
            return false;
        }
        return getMessage().equals(that.getMessage());
    }

    @Override
    public int hashCode() {
        int result = getTimestamp().hashCode();
        result = 31 * result + getMessage().hashCode();
        return result;
    }
}
