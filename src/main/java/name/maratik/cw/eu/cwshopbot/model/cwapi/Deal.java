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
package name.maratik.cw.eu.cwshopbot.model.cwapi;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class Deal {
    private final String sellerId;
    private final String sellerName;
    private final CastleByEmoji sellerCastle;
    private final String buyerId;
    private final String buyerName;
    private final CastleByEmoji buyerCastle;
    private final String item;
    private final int qty;
    private final int price;

    public Deal(
        @JsonProperty("sellerId") String sellerId,
        @JsonProperty("sellerName") String sellerName,
        @JsonProperty("sellerCastle") CastleByEmoji sellerCastle,
        @JsonProperty("buyerId") String buyerId,
        @JsonProperty("buyerName") String buyerName,
        @JsonProperty("buyerCastle") CastleByEmoji buyerCastle,
        @JsonProperty("item") String item,
        @JsonProperty("qty") int qty,
        @JsonProperty("price") int price
    ) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.sellerCastle = sellerCastle;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.buyerCastle = buyerCastle;
        this.item = item;
        this.qty = qty;
        this.price = price;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public CastleByEmoji getSellerCastle() {
        return sellerCastle;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public CastleByEmoji getBuyerCastle() {
        return buyerCastle;
    }

    public String getItem() {
        return item;
    }

    public int getQty() {
        return qty;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Deal{" +
            "sellerId='" + sellerId + '\'' +
            ", sellerName='" + sellerName + '\'' +
            ", sellerCastle=" + sellerCastle +
            ", buyerId='" + buyerId + '\'' +
            ", buyerName='" + buyerName + '\'' +
            ", buyerCastle=" + buyerCastle +
            ", item='" + item + '\'' +
            ", qty=" + qty +
            ", price=" + price +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String sellerId;
        private String sellerName;
        private CastleByEmoji sellerCastle;
        private String buyerId;
        private String buyerName;
        private CastleByEmoji buyerCastle;
        private String item;
        private int qty;
        private int price;

        public Builder setSellerId(String sellerId) {
            this.sellerId = sellerId;
            return this;
        }

        public Builder setSellerName(String sellerName) {
            this.sellerName = sellerName;
            return this;
        }

        public Builder setSellerCastle(CastleByEmoji sellerCastle) {
            this.sellerCastle = sellerCastle;
            return this;
        }

        public Builder setBuyerId(String buyerId) {
            this.buyerId = buyerId;
            return this;
        }

        public Builder setBuyerName(String buyerName) {
            this.buyerName = buyerName;
            return this;
        }

        public Builder setBuyerCastle(CastleByEmoji buyerCastle) {
            this.buyerCastle = buyerCastle;
            return this;
        }

        public Builder setItem(String item) {
            this.item = item;
            return this;
        }

        public Builder setQty(int qty) {
            this.qty = qty;
            return this;
        }

        public Builder setPrice(int price) {
            this.price = price;
            return this;
        }

        public Deal build() {
            return new Deal(sellerId, sellerName, sellerCastle, buyerId, buyerName, buyerCastle, item, qty, price);
        }
    }
}
