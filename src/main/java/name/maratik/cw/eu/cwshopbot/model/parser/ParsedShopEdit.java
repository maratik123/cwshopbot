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

import name.maratik.cw.eu.cwshopbot.model.cwasset.Item;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ParsedShopEdit {
    private final String shopName;
    private final String shopHelpCommand;
    private final int offersCount;
    private final int maxOffersCount;
    private final int shopNumber;
    private final String shopCode;
    private final List<ShopLine> shopLines;

    private ParsedShopEdit(String shopName, String shopHelpCommand, int offersCount, int maxOffersCount,
                           int shopNumber, String shopCode, List<ShopLine> shopLines) {
        this.shopName = Objects.requireNonNull(shopName);
        this.shopHelpCommand = Objects.requireNonNull(shopHelpCommand);
        this.offersCount = offersCount;
        this.maxOffersCount = maxOffersCount;
        this.shopNumber = shopNumber;
        this.shopCode = Objects.requireNonNull(shopCode);
        this.shopLines = Objects.requireNonNull(shopLines);
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopHelpCommand() {
        return shopHelpCommand;
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

    @Override
    public String toString() {
        return "ParsedShopEdit{" +
            "shopName='" + shopName + '\'' +
            ", shopHelpCommand='" + shopHelpCommand + '\'' +
            ", offersCount=" + offersCount +
            ", maxOffersCount=" + maxOffersCount +
            ", shopNumber=" + shopNumber +
            ", shopCode='" + shopCode + '\'' +
            ", shopLines=" + shopLines +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String shopName;
        private String shopHelpCommand;
        private int offersCount;
        private int maxOffersCount;
        private int shopNumber;
        private String shopCode;
        private ImmutableList.Builder<ShopLine> shopLines = ImmutableList.builder();

        public Builder setShopName(String shopName) {
            this.shopName = shopName;
            return this;
        }

        public Builder setShopHelpCommand(String shopHelpCommand) {
            this.shopHelpCommand = shopHelpCommand;
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

        public Builder setShopCode(String shopCode) {
            this.shopCode = shopCode;
            return this;
        }

        public Builder addShopLine(ShopLine shopLine) {
            shopLines.add(Objects.requireNonNull(shopLine));
            return this;
        }

        public ParsedShopEdit build() {
            return new ParsedShopEdit(shopName, shopHelpCommand, offersCount, maxOffersCount, shopNumber,
                shopCode, shopLines.build()
            );
        }
    }

    public static class ShopLine {
        private final Item item;
        private final int mana;
        private final int price;
        private final String deleteCommand;

        private ShopLine(Item item, int mana, int price, String deleteCommand) {
            this.item = Objects.requireNonNull(item);
            this.mana = mana;
            this.price = price;
            this.deleteCommand = Objects.requireNonNull(deleteCommand);
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

        public String getDeleteCommand() {
            return deleteCommand;
        }

        @Override
        public String toString() {
            return "ShopLine{" +
                "item=" + item +
                ", mana=" + mana +
                ", price=" + price +
                ", deleteCommand='" + deleteCommand + '\'' +
                '}';
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Item item;
            private int mana;
            private int price;
            private String deleteCommand;

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

            public Builder setDeleteCommand(String deleteCommand) {
                this.deleteCommand = deleteCommand;
                return this;
            }

            public ShopLine build() {
                return new ShopLine(item, mana, price, deleteCommand);
            }
        }
    }
}
