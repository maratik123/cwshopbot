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

import java.util.Collection;

import static name.maratik.cw.cwshopbot.util.Utils.bool;
import static name.maratik.cw.cwshopbot.util.Utils.number;
import static name.maratik.cw.cwshopbot.util.Utils.text;

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
        jdbcTemplate.update(SAVE_YELLOW_PAGE, saveYellowPageParams(yellowPageEntity));
    }

    @Override
    public void saveAll(Collection<YellowPageEntity> yellowPageEntities) {
        if (yellowPageEntities.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(SAVE_YELLOW_PAGE, yellowPageEntities.stream()
            .map(CustomizedYellowPageRepositoryImpl::saveYellowPageParams)
            .collect(toImmutableList())
        );
    }

    private static Object[] saveYellowPageParams(YellowPageEntity yellowPageEntity) {
        return new Object[]{
            text(yellowPageEntity.getLink()),
            text(yellowPageEntity.getName()),
            text(yellowPageEntity.getOwnerName()),
            text(yellowPageEntity.getOwnerCastle().getCode()),
            text(yellowPageEntity.getProfession().getCode()),
            number(yellowPageEntity.getMana()),
            bool(yellowPageEntity.isActive())
        };
    }
}
