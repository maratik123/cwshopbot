//    cwshopbot
//    Copyright (C) 2018  Marat Bukharov.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package name.maratik.cw.eu.cwshopbot.model;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ShopInfo {
    private final String shopName;
    private final String charName;
    private final String shopCommand;

    public ShopInfo(String shopName, String charName, String shopCommand) {
        this.shopName = shopName;
        this.charName = charName;
        this.shopCommand = shopCommand;
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

    @Override
    public String toString() {
        return "ShopInfo{" +
            "shopName='" + shopName + '\'' +
            ", charName='" + charName + '\'' +
            ", shopCommand='" + shopCommand + '\'' +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String charName;
        private String shopName;
        private String shopCommand;

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

        public ShopInfo build() {
            return new ShopInfo(shopName, charName, shopCommand);
        }
    }
}

