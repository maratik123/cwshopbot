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
package name.maratik.cw.eu.cwshopbot.model.parser;

import name.maratik.cw.eu.cwshopbot.model.ShopPublishStatus;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Item;
import name.maratik.cw.eu.cwshopbot.parser.ParseException;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

import static name.maratik.cw.eu.cwshopbot.application.botcontroller.ShopController.SHOP_COMMAND_PREFIX;
import static name.maratik.cw.eu.cwshopbot.parser.ParserUtils.extractShopCodeFromShopCommand;
import static name.maratik.cw.eu.cwshopbot.parser.ParserUtils.verifyItem;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ParsedShopEdit {
    private final String shopName;
    private final int offersCount;
    private final int maxOffersCount;
    private final int shopNumber;
    private final String shopCode;
    private final String shopCommand;
    private final ShopPublishStatus shopPublishStatus;
    private final List<ShopLine> shopLines;

    private ParsedShopEdit(String shopName, int offersCount, int maxOffersCount, int shopNumber, String shopCode,
                           String shopCommand, ShopPublishStatus shopPublishStatus, List<ShopLine> shopLines) {
        this.shopName = Objects.requireNonNull(shopName);
        this.offersCount = offersCount;
        this.maxOffersCount = maxOffersCount;
        this.shopNumber = shopNumber;
        this.shopCommand = Objects.requireNonNull(shopCommand);
        if (!shopCommand.startsWith(SHOP_COMMAND_PREFIX)) {
            throw new ParseException("Shop command has unexpected format: " + shopCommand);
        }
        this.shopCode = Objects.requireNonNull(shopCode);
        if (!shopCommand.endsWith(shopCode)) {
            throw new ParseException("Shop command '" + shopCommand + "' does not contain shop code: " + shopCode);
        }
        this.shopPublishStatus = Objects.requireNonNull(shopPublishStatus);
        this.shopLines = Objects.requireNonNull(shopLines);
    }

    public String getShopName() {
        return shopName;
    }

    public int getOffersCount() {
        return offersCount;
    }

    public int getMaxOffersCount() {
        return maxOffersCount;
    }

    public int getShopNumber() {
        return shopNumber;
    }

    public String getShopCode() {
        return shopCode;
    }

    public List<ShopLine> getShopLines() {
        return shopLines;
    }

    public String getShopCommand() {
        return shopCommand;
    }

    public ShopPublishStatus getShopPublishStatus() {
        return shopPublishStatus;
    }

    @Override
    public String toString() {
        return "ParsedShopEdit{" +
            "shopName='" + shopName + '\'' +
            ", offersCount=" + offersCount +
            ", maxOffersCount=" + maxOffersCount +
            ", shopNumber=" + shopNumber +
            ", shopCode='" + shopCode + '\'' +
            ", shopCommand='" + shopCommand + '\'' +
            ", shopPublishStatus=" + shopPublishStatus +
            ", shopLines=" + shopLines +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String shopName;
        private int offersCount;
        private int maxOffersCount;
        private int shopNumber;
        private String shopCommand;
        private ShopPublishStatus shopPublishStatus;
        private ImmutableList.Builder<ShopLine> shopLines = ImmutableList.builder();

        public Builder setShopName(String shopName) {
            this.shopName = shopName;
            return this;
        }

        public Builder setOffersCount(int offersCount) {
            this.offersCount = offersCount;
            return this;
        }

        public Builder setMaxOffersCount(int maxOffersCount) {
            this.maxOffersCount = maxOffersCount;
            return this;
        }

        public Builder setShopNumber(int shopNumber) {
            this.shopNumber = shopNumber;
            return this;
        }

        public Builder setShopCommand(String shopCommand) {
            this.shopCommand = shopCommand;
            return this;
        }

        public Builder setShopPublishStatus(ShopPublishStatus shopPublishStatus) {
            this.shopPublishStatus = shopPublishStatus;
            return this;
        }

        public Builder addShopLine(ShopLine shopLine) {
            shopLines.add(Objects.requireNonNull(shopLine));
            return this;
        }

        public ParsedShopEdit build() {
            String shopCode = extractShopCodeFromShopCommand(shopCommand);
            return new ParsedShopEdit(shopName, offersCount, maxOffersCount, shopNumber, shopCode, shopCommand,
                shopPublishStatus, shopLines.build()
            );
        }
    }

    public static class ShopLine {
        private final Item item;
        private final int mana;
        private final int price;

        private ShopLine(Item item, int mana, int price) {
            this.item = Objects.requireNonNull(item);
            this.mana = mana;
            verifyItem(item, mana);
            this.price = price;
        }

        public Item getItem() {
            return item;
        }

        public int getMana() {
            return mana;
        }

        public int getPrice() {
            return price;
        }

        @Override
        public String toString() {
            return "ShopLine{" +
                "item=" + item +
                ", mana=" + mana +
                ", price=" + price +
                '}';
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Item item;
            private int mana;
            private int price;

            public Builder setItem(Item item) {
                this.item = item;
                return this;
            }

            public Builder setMana(int mana) {
                this.mana = mana;
                return this;
            }

            public Builder setPrice(int price) {
                this.price = price;
                return this;
            }

            public ShopLine build() {
                return new ShopLine(item, mana, price);
            }
        }
    }
}
