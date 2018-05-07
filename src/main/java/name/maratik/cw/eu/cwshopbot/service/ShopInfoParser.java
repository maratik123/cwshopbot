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
package name.maratik.cw.eu.cwshopbot.service;

import name.maratik.cw.eu.cwshopbot.model.ShopInfo;
import name.maratik.cw.eu.cwshopbot.model.ShopState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.MessageEntity;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Component
public class ShopInfoParser implements CWParser<ShopInfo> {

    private static final Comparator<MessageEntity> MESSAGE_ENTITY_OFFSET_COMPARATOR =
        Comparator.comparing(MessageEntity::getOffset);

    @Override
    public Optional<ShopInfo> parse(Message message) {
        if (!message.getText().startsWith("Welcome, to the ")) {
            return Optional.empty();
        }
        List<MessageEntity> messageEntities = message.getEntities();
        if (messageEntities == null) {
            return Optional.empty();
        }
        Iterator<MessageEntity> messageEntityIterator = messageEntities.stream()
            .sorted(MESSAGE_ENTITY_OFFSET_COMPARATOR)
            .iterator();
        Optional<String> shopName = extractBoldText(messageEntityIterator).map(MessageEntity::getText);
        if (!shopName.isPresent()) {
            return Optional.empty();
        }
        Optional<String> charName = extractBoldText(messageEntityIterator).map(MessageEntity::getText);
        if (!charName.isPresent()) {
            return Optional.empty();
        }
        Optional<ValueWithNextPointer<ShopState>> shopState = extractBoldText(messageEntityIterator)
            .flatMap(messageEntity -> ValueWithNextPointer.of(messageEntity, ShopState::findByCode));
        Optional<String> shopCommand = messageEntities.stream()
            .max(MESSAGE_ENTITY_OFFSET_COMPARATOR)
            .map(MessageEntity::getText);
        return shopCommand
            .filter(s -> s.startsWith("/ws_"))
            .map(s -> ShopInfo.builder()
                    .setShopName(shopName.get())
                    .setCharName(charName.get())
                    .setShopCommand(s)
                    .build()
            );
    }

    private static Optional<MessageEntity> extractFromIterator(Iterator<MessageEntity> it,
                                                       Function<MessageEntity, Optional<MessageEntity>> extractor) {
        return Optional.of(it)
            .filter(Iterator::hasNext)
            .map(Iterator::next)
            .flatMap(extractor);
    }

    private static Optional<MessageEntity> extractByType(String type, Iterator<MessageEntity> it) {
        return extractFromIterator(it, messageEntity -> {
            if (type.equals(messageEntity.getType())) {
                return Optional.of(messageEntity);
            }
            return Optional.empty();
        });
    }

    private static Optional<MessageEntity> extractBoldText(Iterator<MessageEntity> it) {
        return extractByType("bold", it);
    }

    private static Optional<MessageEntity> extractBotCommand(Iterator<MessageEntity> it) {
        return extractByType("bot_command", it).filter(cmd -> cmd.getText().startsWith("/"));
    }

    private static class ValueWithNextPointer<T> {
        private final int nextPointer;
        private final T value;

        private ValueWithNextPointer(int nextPointer, T value) {
            this.nextPointer = nextPointer;
            this.value = value;
        }

        private static <T> Optional<ValueWithNextPointer<T>> of(MessageEntity messageEntity,
                                                                Function<String, Optional<T>> valueExtractor) {
            return Optional.of(messageEntity.getText())
                .flatMap(valueExtractor)
                .map(value -> new ValueWithNextPointer<>(
                    messageEntity.getOffset() + messageEntity.getLength(),
                    value
                ));
        }

        public int getNextPointer() {
            return nextPointer;
        }

        public T getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "ValueWithNextPointer{" +
                "nextPointer=" + nextPointer +
                ", value=" + value +
                '}';
        }
    }
}
