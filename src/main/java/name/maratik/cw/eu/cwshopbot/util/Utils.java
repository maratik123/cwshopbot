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
package name.maratik.cw.eu.cwshopbot.util;

import name.maratik.cw.eu.cwshopbot.model.cwasset.Item;

import com.google.common.collect.ImmutableList;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.MessageEntity;
import org.telegram.telegrambots.api.objects.User;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class Utils {
    public static final Comparator<MessageEntity> MESSAGE_ENTITY_OFFSET_COMPARATOR =
        Comparator.comparingInt(MessageEntity::getOffset);

    public static int endOfEntity(MessageEntity messageEntity) {
        return messageEntity.getOffset() + messageEntity.getLength();
    }

    public static StringBuilder appendCommandLink(StringBuilder sb, String commandPrefix, Item item) {
        return appendCommandLink(sb, commandPrefix, item.getId());
    }

    public static StringBuilder appendCommandLink(StringBuilder sb, String commandPrefix, String id) {
        return sb.append('[').append(commandPrefix).append(id).append("](")
            .append(commandPrefix).append(id).append(')');
    }

    public static int indexOfNth(String str, @SuppressWarnings("SameParameterValue") int ch, int startFrom,
                                 @SuppressWarnings("SameParameterValue") int n) {
        if (n <= 0) {
            return -1;
        }
        for(int curPos = startFrom, i = n; true; ++curPos) {
            curPos = str.indexOf(ch, curPos);
            if (curPos == -1) {
                return -1;
            }
            --i;
            if (i <= 0) {
                return curPos;
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static Optional<MessageEntity> extractFromIterator(Iterator<MessageEntity> it,
                                                              Function<MessageEntity, Optional<MessageEntity>> extractor) {
        return Optional.of(it)
            .filter(Iterator::hasNext)
            .map(Iterator::next)
            .flatMap(extractor);
    }

    @SuppressWarnings("WeakerAccess")
    public static Optional<MessageEntity> extractByType(MessageType messageType, Iterator<MessageEntity> it) {
        return extractFromIterator(it, messageEntity -> Optional.of(messageEntity)
            .filter(msgEntity -> MessageType.findByCode(msgEntity.getType()).filter(messageType::equals).isPresent())
        );
    }

    public static Optional<MessageEntity> extractBoldText(Iterator<MessageEntity> it) {
        return extractByType(MessageType.BOLD, it);
    }

    public static Optional<MessageEntity> extractBotCommand(Iterator<MessageEntity> it) {
        return extractByType(MessageType.BOT_COMMAND, it).filter(cmd -> cmd.getText().startsWith("/"));
    }

    public static OptionalLong optionalOf(Long l) {
        return l == null ? OptionalLong.empty() : OptionalLong.of(l);
    }

    public static String reformatMessage(Message message) {
        StringBuilder sb = new StringBuilder();
        for (MessageEntity messageEntity : generateTextMessageEntities(message)) {
            Optional<MessageType> messageTypeOpt = MessageType.findByCode(messageEntity.getType());
            if (messageTypeOpt.isPresent()) {
                MessageType messageType = messageTypeOpt.get();
                messageType.getPrefix().ifPresent(sb::append);
                sb.append(messageEntity.getText());
                messageType.getPostfix().ifPresent(sb::append);
            } else {
                sb.append(messageEntity.getText());
            }
        }
        return sb.toString();
    }

    private static List<MessageEntity> generateTextMessageEntities(Message message) {
        List<MessageEntity> entities = message.getEntities().stream()
            .sorted(MESSAGE_ENTITY_OFFSET_COMPARATOR)
            .collect(toImmutableList());
        String text = message.getText();
        int currPos = 0;
        ImmutableList.Builder<MessageEntity> builder = ImmutableList.builder();
        for (MessageEntity entity : entities) {
            int entityOffset = entity.getOffset();
            if (entityOffset > currPos) {
                builder.add(new TextMessageEntity(text.substring(currPos, entityOffset), currPos));
            }
            builder.add(entity);
            currPos = endOfEntity(entity);
        }
        if (currPos < text.length()) {
            builder.add(new TextMessageEntity(text.substring(currPos), currPos));
        }
        return builder.build();
    }

    @SuppressWarnings("ReturnOfNull")
    private static class TextMessageEntity extends MessageEntity {
        private static final String type = MessageType.TEXT.getCode();
        private final String text;
        private final int offset;

        private TextMessageEntity(String text, int offset) {
            this.text = text;
            this.offset = offset;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public Integer getOffset() {
            return offset;
        }

        @Override
        public Integer getLength() {
            return text.length();
        }

        @Override
        public String getUrl() {
            return null;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public User getUser() {
            return null;
        }

        @Override
        public String toString() {
            return "TextMessageEntity{" +
                "text='" + text + '\'' +
                ", offset=" + offset +
                '}';
        }
    }
}
