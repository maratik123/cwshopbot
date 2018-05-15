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

import name.maratik.cw.eu.cwshopbot.model.cwasset.Item;
import name.maratik.cw.eu.cwshopbot.model.parser.ParsedShopEdit;
import name.maratik.cw.eu.cwshopbot.model.parser.ParsedShopInfo;

import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ShopLine {
    private final Item item;
    private final int price;

    public ShopLine(Item item, int price) {
        this.item = Objects.requireNonNull(item);
        this.price = price;
    }

    public Item getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "ShopLine{" +
            "item=" + item +
            ", price=" + price +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ShopLine of(ParsedShopInfo.ShopLine shopLine) {
        return builder()
            .setItem(shopLine.getItem())
            .setPrice(shopLine.getPrice())
            .build();
    }

    public static ShopLine of(ParsedShopEdit.ShopLine shopLine) {
        return builder()
            .setItem(shopLine.getItem())
            .setPrice(shopLine.getPrice())
            .build();
    }

    public static class Builder {
        private Item item;
        private int price;

        public Builder setItem(Item item) {
            this.item = item;
            return this;
        }

        public Builder setPrice(int price) {
            this.price = price;
            return this;
        }

        public ShopLine build() {
            return new ShopLine(item, price);
        }
    }
}
