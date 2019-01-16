//    cwshopbot
//    Copyright (C) 2019  Marat Bukharov.
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
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Value
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

    @Value
    @RequiredArgsConstructor
    private static class MessageEntityKey {
        private final int offset;
        private final int length;
        private final MessageType messageType;

        private MessageEntityKey(MessageEntity messageEntity) {
            this(messageEntity.getOffset(), messageEntity.getLength(), MessageType.findByCode(messageEntity.getType()));
        }
    }
}
