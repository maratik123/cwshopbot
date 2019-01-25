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
package name.maratik.cw.cwshopbot.application.repository.deal;

import name.maratik.cw.cwshopbot.entity.DealEntity;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import static name.maratik.cw.cwshopbot.util.Utils.number;
import static name.maratik.cw.cwshopbot.util.Utils.text;
import static name.maratik.cw.cwshopbot.util.Utils.timestamp;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Repository
public class CustomizedDealRepositoryImpl implements CustomizedDealRepository {
    private static final String SAVE_DEAL = "" +
        "INSERT INTO DEAL(SELLER_ACCOUNT_ID, " +
        "                 BUYER_ACCOUNT_ID, " +
        "                 ITEM, " +
        "                 QTY, " +
        "                 PRICE, " +
        "                 CREATION_TIME)" +
        " VALUES(?, ?, ?, ?, ?, ?)" +
        " RETURNING ID";
    private final JdbcTemplate jdbcTemplate;

    public CustomizedDealRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DealEntity save(DealEntity dealEntity) {
        return dealEntity.withId(jdbcTemplate.queryForObject(SAVE_DEAL, Long.class,
            number(dealEntity.getSellerAccountId()),
            number(dealEntity.getBuyerAccountId()),
            text(dealEntity.getItem()),
            number(dealEntity.getQty()),
            number(dealEntity.getPrice()),
            timestamp(dealEntity.getCreationTime())
        ));
    }
}
