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
package name.maratik.cw.cwshopbot.model.parser;

import name.maratik.cw.cwshopbot.model.Castle;
import name.maratik.cw.cwshopbot.model.Profession;
import name.maratik.cw.cwshopbot.model.ShopState;
import name.maratik.cw.cwshopbot.model.cwasset.Item;
import name.maratik.cw.cwshopbot.parser.ParseException;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;
import java.util.Objects;

import static name.maratik.cw.cwshopbot.application.botcontroller.ShopController.SHOP_COMMAND_PREFIX;
import static name.maratik.cw.cwshopbot.parser.ParserUtils.extractShopCodeFromShopCommand;
import static name.maratik.cw.cwshopbot.parser.ParserUtils.verifyItem;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Value
public class ParsedShopInfo {
    private final String shopName;
    private final String charName;
    private final ShopState shopState;
    private final List<ShopLine> shopLines;
    private final String shopCode;
    private final int shopNumber;
    private final Castle castle;
    private final int currentMana;
    private final int maxMana;
    private final Profession profession;
    private final String shopType;
    private final String shopCommand;

    @Builder
    private ParsedShopInfo(String shopName, String charName, ShopState shopState, @Singular List<ShopLine> shopLines,
                           int shopNumber, Castle castle, int currentMana, int maxMana, Profession profession,
                           String shopType, String shopCommand) {
        this.shopName = Objects.requireNonNull(shopName, "shopName");
        this.charName = Objects.requireNonNull(charName, "charName");
        this.shopState = Objects.requireNonNull(shopState, "shopState");
        this.shopCommand = Objects.requireNonNull(shopCommand, "shopCommand");
        if (!shopCommand.startsWith(SHOP_COMMAND_PREFIX)) {
            throw new ParseException("Shop command has unexpected format: " + shopCommand);
        }
        this.shopCode = extractShopCodeFromShopCommand(shopCommand);
        if (!shopCommand.endsWith(shopCode)) {
            throw new ParseException("Shop command '" + shopCommand + "' does not contain shop code: " + shopCode);
        }
        this.shopLines = ImmutableList.copyOf(Objects.requireNonNull(shopLines, "shopLines"));
        List<ShopLine> unknownLines = shopLines.stream()
            .filter(shopLine -> !shopLine.getCraftCommand().startsWith(shopCommand))
            .collect(toImmutableList());
        if (!unknownLines.isEmpty()) {
            StringBuilder sb = new StringBuilder("Next lines has unexpected command: ");
            unknownLines.forEach(shopLine -> sb
                .append("Item=")
                .append(shopLine.getItem().getId())
                .append(", command=")
                .append(shopLine.getCraftCommand())
                .append('\n'));
            throw new ParseException(sb.toString());
        }
        this.shopNumber = shopNumber;
        this.castle = Objects.requireNonNull(castle, "castle");
        this.currentMana = currentMana;
        this.maxMana = maxMana;
        this.profession = Objects.requireNonNull(profession, "profession");
        this.shopType = Objects.requireNonNull(shopType, "shopType");
        if (!shopName.startsWith(shopType) && !shopName.endsWith(shopType)) {
            throw new ParseException("Shop name '" + shopName + "' does not contain shop type '" + shopType + '\'');
        }
    }

    @Value
    @Builder
    public static class ShopLine {
        private final Item item;
        private final int mana;
        private final int price;
        private final String craftCommand;

        private ShopLine(Item item, int mana, int price, String craftCommand) {
            this.item = Objects.requireNonNull(item, "item");
            verifyItem(item, mana);
            this.mana = mana;
            this.price = price;
            this.craftCommand = Objects.requireNonNull(craftCommand, "craftCommand");
        }
    }
}
