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
package name.maratik.cw.eu.cwshopbot.model;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class Shop {
    private final String shopName;
    private final String charName;
    private final String shopCode;
    private final int maxOffersCount;
    private final String shopCommand;
    private final int shopNumber;
    private final List<ShopLine> shopLines;

    private Shop(String shopName, String charName, String shopCode, int maxOffersCount, String shopCommand,
                 int shopNumber, List<ShopLine> shopLines) {
        this.shopName = Objects.requireNonNull(shopName);
        this.charName = Objects.requireNonNull(charName);
        this.shopCode = Objects.requireNonNull(shopCode);
        this.maxOffersCount = maxOffersCount;
        this.shopCommand = Objects.requireNonNull(shopCommand);
        this.shopNumber = shopNumber;
        this.shopLines = Objects.requireNonNull(shopLines);
    }

    public String getShopName() {
        return shopName;
    }

    public String getCharName() {
        return charName;
    }

    public String getShopCode() {
        return shopCode;
    }

    public int getMaxOffersCount() {
        return maxOffersCount;
    }

    public String getShopCommand() {
        return shopCommand;
    }

    public int getShopNumber() {
        return shopNumber;
    }

    public List<ShopLine> getShopLines() {
        return shopLines;
    }

    @Override
    public String toString() {
        return "Shop{" +
            "shopName='" + shopName + '\'' +
            ", charName='" + charName + '\'' +
            ", shopCode='" + shopCode + '\'' +
            ", maxOffersCount=" + maxOffersCount +
            ", shopCommand='" + shopCommand + '\'' +
            ", shopNumber=" + shopNumber +
            ", shopLines=" + shopLines +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String shopName;
        private String charName;
        private String shopCode;
        private int maxOffersCount;
        private String shopCommand;
        private int shopNumber;
        private ImmutableList.Builder<ShopLine> shopLines = ImmutableList.builder();

        public Builder setShopName(String shopName) {
            this.shopName = shopName;
            return this;
        }

        public Builder setCharName(String charName) {
            this.charName = charName;
            return this;
        }

        public Builder setShopCode(String shopCode) {
            this.shopCode = shopCode;
            return this;
        }

        public Builder setMaxOffersCount(int maxOffersCount) {
            this.maxOffersCount = maxOffersCount;
            return this;
        }

        public Builder setShopCommand(String shopCommand) {
            this.shopCommand = shopCommand;
            return this;
        }

        public Builder setShopNumber(int shopNumber) {
            this.shopNumber = shopNumber;
            return this;
        }

        public Builder addShopLine(ShopLine shopLine) {
            this.shopLines.add(Objects.requireNonNull(shopLine));
            return this;
        }

        public Shop build() {
            return new Shop(shopName, charName, shopCode, maxOffersCount, shopCommand, shopNumber, shopLines.build());
        }
    }
}
