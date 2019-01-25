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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Value
@Builder
@Table("account")
public class AccountEntity {
    @Id
    private final Long id;
    @NonNull
    private final String externalId;
    @NonNull
    private final String name;
    @NonNull
    private final Castle castle;
    @NonNull
    private final LocalDateTime creationTime;

    public AccountEntity withIdAndCreationTime(IdWithCreationTime idAndCreationTime) {
        return new AccountEntity(idAndCreationTime.getId(), externalId, name, castle,
            idAndCreationTime.getCreationTime()
        );
    }

    public static AccountEntity ofSeller(Deal deal, LocalDateTime creationTime) {
        return new AccountEntity(
            null,
            deal.getSellerId(),
            deal.getSellerName(),
            deal.getSellerCastle().getCastle(),
            creationTime
        );
    }

    public static AccountEntity ofBuyer(Deal deal, LocalDateTime creationTime) {
        return new AccountEntity(
            null,
            deal.getBuyerId(),
            deal.getBuyerName(),
            deal.getBuyerCastle().getCastle(),
            creationTime
        );
    }

    @Value
    public static class IdWithCreationTime {
        private final long id;
        @NonNull
        private final LocalDateTime creationTime;

        public static IdWithCreationTime rowMapper(ResultSet rs, @SuppressWarnings("unused") int i) throws SQLException {
            return new IdWithCreationTime(
                rs.getLong("id"),
                rs.getObject("creation_time", LocalDateTime.class)
            );
        }
    }
}
