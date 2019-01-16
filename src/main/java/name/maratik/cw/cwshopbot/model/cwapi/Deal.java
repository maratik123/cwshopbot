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
package name.maratik.cw.cwshopbot.model.cwapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Value
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
}
