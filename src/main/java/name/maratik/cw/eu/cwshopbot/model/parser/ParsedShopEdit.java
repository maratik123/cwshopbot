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

import com.google.common.collect.ImmutableList;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Item;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ParsedShopEdit {
    private final String shopName;
    private final String shopHelpCommand;
    private final int offerCount;
    private final int maxOfferCount;
    private final String shopCommand;
    private final List<ShopLine> shopLines;

    private ParsedShopEdit(String shopName, String shopHelpCommand, int offerCount, int maxOfferCount, String shopCommand, List<ShopLine> shopLines) {
        this.shopName = Objects.requireNonNull(shopName);
        this.shopHelpCommand = Objects.requireNonNull(shopHelpCommand);
        this.offerCount = offerCount;
        this.maxOfferCount = maxOfferCount;
        this.shopCommand = Objects.requireNonNull(shopCommand);
        this.shopLines = Objects.requireNonNull(shopLines);
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopHelpCommand() {
        return shopHelpCommand;
    }

    public int getOfferCount() {
        return offerCount;
    }

    public int getMaxOfferCount() {
        return maxOfferCount;
    }

    public String getShopCommand() {
        return shopCommand;
    }

    public List<ShopLine> getShopLines() {
        return shopLines;
    }

    @Override
    public String toString() {
        return "ParsedShopEdit{" +
            "shopName='" + shopName + '\'' +
            ", shopHelpCommand='" + shopHelpCommand + '\'' +
            ", offerCount=" + offerCount +
            ", maxOfferCount=" + maxOfferCount +
            ", shopCommand='" + shopCommand + '\'' +
            ", shopLines=" + shopLines +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String shopName;
        private String shopHelpCommand;
        private int offerCount;
        private int maxOfferCount;
        private String shopCommand;
        private ImmutableList.Builder<ShopLine> shopLines = ImmutableList.builder();

        public Builder setShopName(String shopName) {
            this.shopName = shopName;
            return this;
        }

        public Builder setShopHelpCommand(String shopHelpCommand) {
            this.shopHelpCommand = shopHelpCommand;
            return this;
        }

        public Builder setOfferCount(int offerCount) {
            this.offerCount = offerCount;
            return this;
        }

        public Builder setMaxOfferCount(int maxOfferCount) {
            this.maxOfferCount = maxOfferCount;
            return this;
        }

        public Builder setShopCommand(String shopCommand) {
            this.shopCommand = shopCommand;
            return this;
        }

        public Builder addShopLine(ShopLine shopLine) {
            shopLines.add(shopLine);
            return this;
        }

        public ParsedShopEdit build() {
            return new ParsedShopEdit(shopName, shopHelpCommand, offerCount, maxOfferCount, shopCommand, shopLines.build());
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
