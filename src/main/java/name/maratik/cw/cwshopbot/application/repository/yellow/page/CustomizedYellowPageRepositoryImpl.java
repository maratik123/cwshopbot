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
package name.maratik.cw.cwshopbot.application.repository.yellow.page;

import name.maratik.cw.cwshopbot.entity.YellowPageEntity;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Repository
public class CustomizedYellowPageRepositoryImpl implements CustomizedYellowPageRepository {
    private static final String SAVE_YELLOW_PAGE = "" +
        "INSERT INTO YELLOW_PAGE(LINK, NAME, OWNER_NAME, OWNER_CASTLE, PROFESSION, MANA, ACTIVE)" +
        "  VALUES(?, ?, ?, ?, ?, ?, ?)" +
        "  ON CONFLICT (LINK) DO UPDATE SET" +
        "    NAME = EXCLUDED.NAME," +
        "    OWNER_NAME = EXCLUDED.OWNER_NAME," +
        "    OWNER_CASTLE = EXCLUDED.OWNER_CASTLE," +
        "    PROFESSION = EXCLUDED.PROFESSION," +
        "    MANA = EXCLUDED.MANA," +
        "    ACTIVE = EXCLUDED.ACTIVE";
    private final JdbcTemplate jdbcTemplate;

    public CustomizedYellowPageRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(YellowPageEntity yellowPageEntity) {
        jdbcTemplate.update(SAVE_YELLOW_PAGE,
            yellowPageEntity.getLink(), yellowPageEntity.getName(), yellowPageEntity.getOwnerName(),
            yellowPageEntity.getOwnerCastle().getCode(), yellowPageEntity.getProfession().getCode(),
            yellowPageEntity.getMana(), yellowPageEntity.isActive()
        );
    }

    @Override
    public void saveAll(List<YellowPageEntity> yellowPageEntities) {
        jdbcTemplate.batchUpdate(SAVE_YELLOW_PAGE, yellowPageEntities.stream()
            .map(yellowPageEntity -> new Object[]{
                yellowPageEntity.getLink(), yellowPageEntity.getName(), yellowPageEntity.getOwnerName(),
                yellowPageEntity.getOwnerCastle().getCode(), yellowPageEntity.getProfession().getCode(),
                yellowPageEntity.getMana(), yellowPageEntity.isActive()
            })
            .collect(toImmutableList())
        );
    }
}