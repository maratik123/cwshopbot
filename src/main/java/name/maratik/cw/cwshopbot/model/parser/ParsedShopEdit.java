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

import name.maratik.cw.cwshopbot.model.ShopPublishStatus;
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

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Value
public class ParsedShopEdit {
    private final String shopName;
    private final int offersCount;
    private final int maxOffersCount;
    private final int shopNumber;
    private final String shopCode;
    private final String shopCommand;
    private final ShopPublishStatus shopPublishStatus;
    private final List<ShopLine> shopLines;

    @Builder
    private ParsedShopEdit(String shopName, int offersCount, int maxOffersCount, int shopNumber,
                           String shopCommand, ShopPublishStatus shopPublishStatus,
                           @Singular ImmutableList<ShopLine> shopLines) {
        this.shopName = Objects.requireNonNull(shopName, "shopName");
        this.offersCount = offersCount;
        this.maxOffersCount = maxOffersCount;
        this.shopNumber = shopNumber;
        this.shopCommand = Objects.requireNonNull(shopCommand, "shopCommand");
        if (!shopCommand.startsWith(SHOP_COMMAND_PREFIX)) {
            throw new ParseException("Shop command has unexpected format: " + shopCommand);
        }
        this.shopCode = extractShopCodeFromShopCommand(shopCommand);
        if (!shopCommand.endsWith(shopCode)) {
            throw new ParseException("Shop command '" + shopCommand + "' does not contain shop code: " + shopCode);
        }
        this.shopPublishStatus = Objects.requireNonNull(shopPublishStatus, "shopPublishStatus");
        this.shopLines = Objects.requireNonNull(shopLines, "shopLines");
    }

    @Builder
    @Value
    public static class ShopLine {
        private final Item item;
        private final int mana;
        private final int price;

        private ShopLine(Item item, int mana, int price) {
            this.item = Objects.requireNonNull(item, "item");
            this.mana = mana;
            verifyItem(item, mana);
            this.price = price;
        }
    }
}
