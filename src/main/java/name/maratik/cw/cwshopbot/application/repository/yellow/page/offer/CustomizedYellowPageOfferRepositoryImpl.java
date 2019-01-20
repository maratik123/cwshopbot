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
package name.maratik.cw.cwshopbot.application.repository.yellow.page.offer;

import name.maratik.cw.cwshopbot.entity.YellowPageOfferEntity;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

import static name.maratik.cw.cwshopbot.util.Utils.bool;
import static name.maratik.cw.cwshopbot.util.Utils.number;
import static name.maratik.cw.cwshopbot.util.Utils.text;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Repository
public class CustomizedYellowPageOfferRepositoryImpl implements CustomizedYellowPageOfferRepository {
    private static final String SAVE_YELLOW_PAGE_OFFER = "" +
        "INSERT INTO YELLOW_PAGE_OFFER(YELLOW_PAGE, ITEM, PRICE, MANA, ACTIVE)" +
        "  VALUES(?, ?, ?, ?, ?)" +
        "  ON CONFLICT(YELLOW_PAGE, ITEM) DO UPDATE SET " +
        "    PRICE = EXCLUDED.PRICE," +
        "    MANA = EXCLUDED.MANA," +
        "    ACTIVE = EXCLUDED.ACTIVE";
    private final JdbcTemplate jdbcTemplate;

    public CustomizedYellowPageOfferRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(YellowPageOfferEntity yellowPageOfferEntity) {
        jdbcTemplate.update(SAVE_YELLOW_PAGE_OFFER, saveYellowPageOfferParams(yellowPageOfferEntity));
    }

    @Override
    public void saveAll(Collection<YellowPageOfferEntity> yellowPageOfferEntities) {
        if (yellowPageOfferEntities.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(SAVE_YELLOW_PAGE_OFFER, yellowPageOfferEntities.stream()
            .map(CustomizedYellowPageOfferRepositoryImpl::saveYellowPageOfferParams)
            .collect(toImmutableList())
        );
    }

    private static Object[] saveYellowPageOfferParams(YellowPageOfferEntity yellowPageOfferEntity) {
        return new Object[]{
            text(yellowPageOfferEntity.getYellowPage()),
            text(yellowPageOfferEntity.getItem()),
            number(yellowPageOfferEntity.getPrice()),
            number(yellowPageOfferEntity.getMana()),
            bool(yellowPageOfferEntity.isActive())
        };
    }
}
