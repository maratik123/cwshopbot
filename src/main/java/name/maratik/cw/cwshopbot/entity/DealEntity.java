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
package name.maratik.cw.cwshopbot.entity;

import name.maratik.cw.cwshopbot.model.Castle;
import name.maratik.cw.cwshopbot.model.cwapi.Deal;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Value
@Builder(toBuilder = true)
@Table("deal")
public class DealEntity {
    @Id
    @Nullable
    private final Long id;
    @NonNull
    private final String sellerId;
    @NonNull
    private final String sellerName;
    @NonNull
    private final Castle sellerCastle;
    @NonNull
    private final String buyerId;
    @NonNull
    private final String buyerName;
    @NonNull
    private final Castle buyerCastle;
    @NonNull
    private final String item;
    private final int qty;
    private final int price;
    @NonNull
    private final LocalDateTime creationTime;

    public static DealEntity of(@NonNull Deal deal, @NonNull LocalDateTime creationTime) {
        return new DealEntity(
            null,
            deal.getSellerId(),
            deal.getSellerName(),
            deal.getSellerCastle().getCastle(),
            deal.getBuyerId(),
            deal.getBuyerName(),
            deal.getBuyerCastle().getCastle(),
            deal.getItem(),
            deal.getQty(),
            deal.getPrice(),
            creationTime
        );
    }

    public DealEntity withId(Long id) {
        if (Objects.equals(id, this.id)) {
            return this;
        }
        return new DealEntity(id, sellerId, sellerName, sellerCastle, buyerId, buyerName, buyerCastle, item, qty, price, creationTime);
    }

    @Value
    @Table("deal")
    public static class Key {
        private final long id;

        public static Key rowMapper(ResultSet rs, @SuppressWarnings("unused") int i) throws SQLException {
            return new Key(rs.getLong("id"));
        }
    }
}
