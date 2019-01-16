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
package name.maratik.cw.cwshopbot.application.repository.yellow.page.specialization;

import name.maratik.cw.cwshopbot.entity.YellowPageSpecializationEntity;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Repository
public class CustomizedYellowPageSpecializationRepositoryImpl implements CustomizedYellowPageSpecializationRepository {
    private static final String SAVE_YELLOW_PAGE_SPECIALIZATION = "" +
        "INSERT INTO YELLOW_PAGE_SPECIALIZATION(YELLOW_PAGE, SPECIALIZATION, VALUE)" +
        "  VALUES(?, ?, ?)" +
        "  ON CONFLICT(YELLOW_PAGE, SPECIALIZATION) DO UPDATE SET " +
        "    VALUE = EXCLUDED.VALUE";
    private final JdbcTemplate jdbcTemplate;

    public CustomizedYellowPageSpecializationRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(YellowPageSpecializationEntity yellowPageSpecializationEntity) {
        jdbcTemplate.update(SAVE_YELLOW_PAGE_SPECIALIZATION,
            yellowPageSpecializationEntity.getYellowPage(), yellowPageSpecializationEntity.getSpecialization(),
            yellowPageSpecializationEntity.getValue()
        );
    }

    @Override
    public void saveAll(List<YellowPageSpecializationEntity> yellowPageSpecializationEntities) {
        if (yellowPageSpecializationEntities.isEmpty()) {
            return;
        }
        jdbcTemplate.batchUpdate(SAVE_YELLOW_PAGE_SPECIALIZATION, yellowPageSpecializationEntities.stream()
            .map(yellowPageSpecializationEntity -> new Object[]{
                yellowPageSpecializationEntity.getYellowPage(), yellowPageSpecializationEntity.getSpecialization(),
                yellowPageSpecializationEntity.getValue()
            })
            .collect(toImmutableList())
        );
    }
}
