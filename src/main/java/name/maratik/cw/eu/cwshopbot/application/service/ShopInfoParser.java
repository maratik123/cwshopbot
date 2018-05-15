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
package name.maratik.cw.eu.cwshopbot.application.service;

import name.maratik.cw.eu.cwshopbot.model.ShopState;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Item;
import name.maratik.cw.eu.cwshopbot.model.parser.ParsedShopInfo;
import name.maratik.cw.eu.cwshopbot.util.MessageType;
import name.maratik.cw.eu.cwshopbot.util.ValueWithNextPointer;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.MessageEntity;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static name.maratik.cw.eu.cwshopbot.util.Emoji.GOLD;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.GOLD_LEN;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.MANA;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.MANA_LEN;
import static name.maratik.cw.eu.cwshopbot.util.Utils.MESSAGE_ENTITY_OFFSET_COMPARATOR;
import static name.maratik.cw.eu.cwshopbot.util.Utils.endOfEntity;
import static name.maratik.cw.eu.cwshopbot.util.Utils.extractBoldText;
import static name.maratik.cw.eu.cwshopbot.util.Utils.extractBotCommand;
import static name.maratik.cw.eu.cwshopbot.util.Utils.indexOfNth;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Component
public class ShopInfoParser implements CWParser<ParsedShopInfo> {

    private static final String SHOP_COMMAND_PREFIX = "/ws_";
    private static final int SHOP_COMMAND_PREFIX_LEN = SHOP_COMMAND_PREFIX.length();
    private final ItemSearchService itemSearchService;

    public ShopInfoParser(ItemSearchService itemSearchService) {
        this.itemSearchService = itemSearchService;
    }

    @Override
    public Optional<ParsedShopInfo> parse(Message message) {
        String messageText = message.getText();
        if (messageText == null || !messageText.startsWith("Welcome, to the ")) {
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

        ParsedShopInfo.Builder parsedShopInfoBuilder = ParsedShopInfo.builder()
            .setShopName(shopName.get());

        Optional<String> charName = extractBoldText(messageEntityIterator).map(MessageEntity::getText);
        if (!charName.isPresent()) {
            return Optional.empty();
        }
        parsedShopInfoBuilder.setCharName(charName.get());

        Optional<ValueWithNextPointer<ShopState>> shopState = extractBoldText(messageEntityIterator)
            .flatMap(messageEntity -> ValueWithNextPointer.of(messageEntity, ShopState::findByCode));
        if (!shopState.isPresent()) {
            return Optional.empty();
        }
        parsedShopInfoBuilder.setShopState(shopState.get().getValue());

        Optional<String> shopCommand = messageEntities.stream()
            .max(MESSAGE_ENTITY_OFFSET_COMPARATOR)
            .filter(messageEntity ->
                MessageType.findByCode(messageEntity.getType()).filter(MessageType.BOT_COMMAND::equals).isPresent()
            ).map(MessageEntity::getText)
            .filter(s -> s.startsWith(SHOP_COMMAND_PREFIX));
        if (!shopCommand.isPresent()) {
            return Optional.empty();
        }
        parsedShopInfoBuilder.setShopCommand(shopCommand.get())
            .setShopCode(shopCommand.get().substring(SHOP_COMMAND_PREFIX_LEN));

        int nextPointer = shopState.get().getNextPointer();
        nextPointer = indexOfNth(messageText, '\n', nextPointer, 2) + 1;
        if (nextPointer == 0) {
            return Optional.empty();
        }
        Optional<ValueWithNextPointer<ParsedShopInfo.ShopLine>> optionalShopLine =
            parseShopLine(message, messageEntityIterator, nextPointer);
        if (!optionalShopLine.isPresent()) {
            return Optional.empty();
        }
        do {
            ValueWithNextPointer<ParsedShopInfo.ShopLine> shopLine = optionalShopLine.get();
            if (!shopLine.getValue().getCraftCommand().startsWith(shopCommand.get())) {
                return Optional.empty();
            }
            parsedShopInfoBuilder.addShopLine(shopLine.getValue());
            nextPointer = shopLine.getNextPointer();
            optionalShopLine = parseShopLine(message, messageEntityIterator, nextPointer);
        } while (optionalShopLine.isPresent());

        return Optional.of(parsedShopInfoBuilder.build());
    }

    private Optional<ValueWithNextPointer<ParsedShopInfo.ShopLine>> parseShopLine(Message message, Iterator<MessageEntity> messageEntityIterator, int nextPointer) {
        String messageText = message.getText();
        int commaAfterName = messageText.indexOf(',', nextPointer);
        if (commaAfterName == -1) {
            return Optional.empty();
        }
        String itemName = messageText.substring(nextPointer, commaAfterName);
        List<Item> foundItems = itemSearchService.findItemByNameList(itemName, false);
        if (foundItems.size() != 1) {
            return Optional.empty();
        }
        int manaChar = messageText.indexOf(MANA, commaAfterName);
        if (manaChar == -1) {
            return Optional.empty();
        }
        int mana;
        try {
            mana = Integer.parseInt(messageText.substring(commaAfterName + 1, manaChar).trim());
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        int goldChar = messageText.indexOf(GOLD, manaChar + MANA_LEN);
        if (goldChar == -1) {
            return Optional.empty();
        }
        int price;
        try {
            price = Integer.parseInt(messageText.substring(manaChar + MANA_LEN, goldChar).trim());
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        return extractBotCommand(messageEntityIterator)
            .filter(messageEntity -> messageEntity.getOffset() >= goldChar + GOLD_LEN)
            .flatMap(commandEntity -> Optional.of(
                    messageText.indexOf('\n', endOfEntity(commandEntity))
                )
                .filter(nextNewLine -> nextNewLine != -1)
                .map(nextNewLine -> new ValueWithNextPointer<>(nextNewLine + 1,
                    ParsedShopInfo.ShopLine.builder()
                        .setItem(foundItems.get(0))
                        .setMana(mana)
                        .setPrice(price)
                        .setCraftCommand(commandEntity.getText())
                        .build()
                )));
    }
}
