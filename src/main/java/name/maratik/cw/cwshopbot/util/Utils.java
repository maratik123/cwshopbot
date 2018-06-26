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
package name.maratik.cw.cwshopbot.util;

import name.maratik.cw.cwshopbot.model.cwasset.Item;

import com.google.common.collect.ImmutableList;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.MessageEntity;
import org.telegram.telegrambots.api.objects.User;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@SuppressWarnings("WeakerAccess")
public class Utils {
    public static final Comparator<MessageEntity> MESSAGE_ENTITY_OFFSET_COMPARATOR =
        Comparator.comparingInt(MessageEntity::getOffset);
    public static final Object[] EMPTY = new Object[0];

    private static final int MAX_PREFIX_LEN = extractMaxPropLenFromMessageType(MessageType::getPrefix);
    private static final int MAX_POSTFIX_LEN = extractMaxPropLenFromMessageType(MessageType::getPostfix);
    private static final int MESSAGE_ENTITY_OVERHEAD = MAX_PREFIX_LEN + MAX_POSTFIX_LEN;

    public static int endOfEntity(MessageEntity messageEntity) {
        return messageEntity.getOffset() + messageEntity.getLength();
    }

    public static String createCommandLink(String commandPrefix, Item item) {
        return appendCommandLink(new StringBuilder(), commandPrefix, item).toString();
    }

    public static String createCommandLink(String commandPrefix, String id) {
        return appendCommandLink(new StringBuilder(), commandPrefix, id).toString();
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
            .filter(msgEntity -> MessageType.findByCode(msgEntity.getType()) == messageType)
        );
    }

    public static Optional<MessageEntity> extractBoldText(Iterator<MessageEntity> it) {
        return extractByType(MessageType.BOLD, it);
    }

    public static Optional<MessageEntity> extractBotCommand(Iterator<MessageEntity> it) {
        return extractByType(MessageType.BOT_COMMAND, it).filter(cmd -> cmd.getText().startsWith("/"));
    }

    public static String reformatMessage(Message message) {
        char[] text = message.getText().toCharArray();
        List<MessageEntity> messageEntities = generateTextMessageEntities(message);
        StringBuilder sb = new StringBuilder(
            text.length + MESSAGE_ENTITY_OVERHEAD * messageEntities.size()
        );
        for (MessageEntity messageEntity : messageEntities) {
            MessageType messageType = MessageType.findByCode(messageEntity.getType());
            messageType.getPrefix().ifPresent(sb::append);
            sb.append(text, messageEntity.getOffset(), messageEntity.getLength());
            messageType.getPostfix().ifPresent(sb::append);
        }
        return sb.toString();
    }

    public static <T> Stream<T> stream(Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }

    private static List<MessageEntity> generateTextMessageEntities(Message message) {
        List<MessageEntity> entities = message.getEntities().stream()
            .sorted(MESSAGE_ENTITY_OFFSET_COMPARATOR)
            .collect(toImmutableList());
        int currPos = 0;
        ImmutableList.Builder<MessageEntity> builder = ImmutableList.builder();
        for (MessageEntity entity : entities) {
            int entityOffset = entity.getOffset();
            if (entityOffset > currPos) {
                builder.add(new TextMessageEntity(currPos, entityOffset - currPos));
            }
            builder.add(entity);
            currPos = entityOffset + entity.getLength();
        }
        int textLen = message.getText().length();
        if (currPos < textLen) {
            builder.add(new TextMessageEntity(currPos, textLen - currPos));
        }
        return builder.build();
    }

    private static int extractMaxPropLenFromMessageType(Function<MessageType, Optional<String>> extractor) {
        return Arrays.stream(MessageType.values())
            .map(extractor)
            .flatMap(Utils::stream)
            .mapToInt(String::length)
            .max()
            .orElse(0);
    }

    @SuppressWarnings("ReturnOfNull")
    private static class TextMessageEntity extends MessageEntity {
        private static final String type = MessageType.TEXT.getCode();
        private static final long serialVersionUID = -1361075071106423195L;
        private final int offset;
        private final int length;

        private TextMessageEntity(int offset, int length) {
            this.offset = offset;
            this.length = length;
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
            return length;
        }

        @Override
        public String getUrl() {
            return null;
        }

        @Override
        public String getText() {
            return null;
        }

        @Override
        public User getUser() {
            return null;
        }

        @Override
        public String toString() {
            return "TextMessageEntity{" +
                "offset=" + offset +
                ", length=" + length +
                '}';
        }
    }
}
