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

import java.util.List;

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
        jdbcTemplate.update(SAVE_YELLOW_PAGE_OFFER,
            yellowPageOfferEntity.getYellowPage(), yellowPageOfferEntity.getItem(), yellowPageOfferEntity.getPrice(),
            yellowPageOfferEntity.getMana(), yellowPageOfferEntity.isActive());
    }

    @Override
    public void saveAll(List<YellowPageOfferEntity> yellowPageOfferEntities) {
        if (yellowPageOfferEntities.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(SAVE_YELLOW_PAGE_OFFER, yellowPageOfferEntities.stream()
            .map(yellowPageOfferEntity -> new Object[]{
                yellowPageOfferEntity.getYellowPage(), yellowPageOfferEntity.getItem(),
                yellowPageOfferEntity.getPrice(), yellowPageOfferEntity.getMana(), yellowPageOfferEntity.isActive()
            })
            .collect(toImmutableList())
        );
    }
}
