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

import name.maratik.cw.eu.cwshopbot.model.ShopState;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ParsedShopInfo {
    private final String shopName;
    private final String charName;
    private final String shopCommand;
    private final ShopState shopState;

    public ParsedShopInfo(String shopName, String charName, String shopCommand, ShopState shopState) {
        this.shopName = shopName;
        this.charName = charName;
        this.shopCommand = shopCommand;
        this.shopState = shopState;
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

    @Override
    public String toString() {
        return "ShopInfo{" +
            "shopName='" + shopName + '\'' +
            ", charName='" + charName + '\'' +
            ", shopCommand='" + shopCommand + '\'' +
            ", shopState=" + shopState +
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

        public Builder setCharName(String charName) {
            this.charName = charName;
            return this;
        }

        public Builder setShopName(String shopName) {
            this.shopName = shopName;
            return this;
        }

        public Builder setShopCommand(String shopCommand) {
            this.shopCommand = shopCommand;
            return this;
        }

        public Builder setShopState(ShopState shopState) {
            this.shopState = shopState;
            return this;
        }

        public ParsedShopInfo build() {
            return new ParsedShopInfo(shopName, charName, shopCommand, shopState);
        }
    }

    /**
     * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
     */
    public static class ShopLine {
        private final String itemName;
        private final int mana;
        private final int price;
        private final String craftCommand;

        public ShopLine(String itemName, int mana, int price, String craftCommand) {
            this.itemName = itemName;
            this.mana = mana;
            this.price = price;
            this.craftCommand = craftCommand;
        }

        public String getItemName() {
            return itemName;
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
                "itemName='" + itemName + '\'' +
                ", mana=" + mana +
                ", price=" + price +
                ", craftCommand='" + craftCommand + '\'' +
                '}';
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String itemName;
            private int mana;
            private int price;
            private String craftCommand;

            public Builder setItemName(String itemName) {
                this.itemName = itemName;
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
                return new ShopLine(itemName, mana, price, craftCommand);
            }
        }
    }
}

