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
import name.maratik.cw.eu.cwshopbot.model.ShopState;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Item;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ParsedShopInfo {
    private final String shopName;
    private final String charName;
    private final String shopCommand;
    private final ShopState shopState;
    private final List<ShopLine> shopLines;

    private ParsedShopInfo(String shopName, String charName, String shopCommand, ShopState shopState,
                           List<ShopLine> shopLines) {
        this.shopName = Objects.requireNonNull(shopName);
        this.charName = Objects.requireNonNull(charName);
        this.shopCommand = Objects.requireNonNull(shopCommand);
        this.shopState = Objects.requireNonNull(shopState);
        this.shopLines = Objects.requireNonNull(shopLines);
    }

    public String getShopName() {
        return shopName;
    }

    public String getCharName() {
        return charName;
    }

    public String getShopCommand() {
        return shopCommand;
    }

    public ShopState getShopState() {
        return shopState;
    }

    public List<ShopLine> getShopLines() {
        return shopLines;
    }

    @Override
    public String toString() {
        return "ParsedShopInfo{" +
            "shopName='" + shopName + '\'' +
            ", charName='" + charName + '\'' +
            ", shopCommand='" + shopCommand + '\'' +
            ", shopState=" + shopState +
            ", shopLines=" + shopLines +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String charName;
        private String shopName;
        private String shopCommand;
        private ShopState shopState;
        private final ImmutableList.Builder<ShopLine> shopLines = ImmutableList.builder();

        @SuppressWarnings("UnusedReturnValue")
        public Builder setCharName(String charName) {
            this.charName = charName;
            return this;
        }

        public Builder setShopName(String shopName) {
            this.shopName = shopName;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder setShopCommand(String shopCommand) {
            this.shopCommand = shopCommand;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder setShopState(ShopState shopState) {
            this.shopState = shopState;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder addShopLine(ShopLine shopLine) {
            shopLines.add(shopLine);
            return this;
        }

        public ParsedShopInfo build() {
            return new ParsedShopInfo(shopName, charName, shopCommand, shopState, shopLines.build());
        }
    }

    public static class ShopLine {
        private final Item item;
        private final int mana;
        private final int price;
        private final String craftCommand;

        @SuppressWarnings("WeakerAccess")
        public ShopLine(Item item, int mana, int price, String craftCommand) {
            this.item = Objects.requireNonNull(item);
            this.mana = mana;
            this.price = price;
            this.craftCommand = Objects.requireNonNull(craftCommand);
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

        public String getCraftCommand() {
            return craftCommand;
        }

        @Override
        public String toString() {
            return "ShopLine{" +
                "item=" + item +
                ", mana=" + mana +
                ", price=" + price +
                ", craftCommand='" + craftCommand + '\'' +
                '}';
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Item item;
            private int mana;
            private int price;
            private String craftCommand;

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

            public Builder setCraftCommand(String craftCommand) {
                this.craftCommand = craftCommand;
                return this;
            }

            public ShopLine build() {
                return new ShopLine(item, mana, price, craftCommand);
            }
        }
    }
}

