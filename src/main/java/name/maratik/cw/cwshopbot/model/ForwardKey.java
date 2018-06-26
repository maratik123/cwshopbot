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
package name.maratik.cw.cwshopbot.model;

import name.maratik.cw.cwshopbot.util.MessageType;

import com.google.common.collect.ImmutableSet;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.MessageEntity;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ForwardKey {
    private final Instant timestamp;
    private final Set<MessageEntityKey> entities;
    private final String text;

    public ForwardKey(Message message) {
        Objects.requireNonNull(message, "message");
        timestamp = Instant.ofEpochSecond(
            Objects.requireNonNull(message.getForwardDate(), "message.forwardDate")
        );
        text = Objects.requireNonNull(message.getText(), "message.text");
        this.entities = Optional.ofNullable(message.getEntities())
            .map(entities -> entities.stream()
                .map(MessageEntityKey::new)
                .collect(toImmutableSet())
            ).orElseGet(ImmutableSet::of);
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

        if (!timestamp.equals(that.timestamp)) {
            return false;
        }
        if (!entities.equals(that.entities)) {
            return false;
        }
        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        int result = timestamp.hashCode();
        result = 31 * result + entities.hashCode();
        result = 31 * result + text.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ForwardKey{" +
            "timestamp=" + timestamp +
            ", text='" + text + '\'' +
            ", entities=" + entities +
            '}';
    }

    private static class MessageEntityKey {
        private final int offset;
        private final int length;
        private final MessageType messageType;

        private MessageEntityKey(int offset, int length, MessageType messageType) {
            this.offset = offset;
            this.length = length;
            this.messageType = Objects.requireNonNull(messageType, "messageType");
        }

        private MessageEntityKey(MessageEntity messageEntity) {
            this(messageEntity.getOffset(), messageEntity.getLength(), MessageType.findByCode(messageEntity.getType()));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof MessageEntityKey)) {
                return false;
            }

            MessageEntityKey that = (MessageEntityKey) o;

            if (offset != that.offset) {
                return false;
            }
            if (length != that.length) {
                return false;
            }
            return messageType == that.messageType;
        }

        @Override
        public int hashCode() {
            int result = offset;
            result = 31 * result + length;
            result = 31 * result + messageType.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "MessageKey{" +
                "offset=" + offset +
                ", length=" + length +
                ", messageType=" + messageType +
                '}';
        }
    }
}
