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

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public interface YellowPageRepository extends Repository<YellowPageEntity, String>, CustomizedYellowPageRepository {
    @Transactional(readOnly = true)
    @Query("" +
        "SELECT link, name, owner_name, owner_castle, profession, mana, max_mana, active, last_active_time" +
        "  FROM yellow_page" +
        " WHERE link = :link")
    Optional<YellowPageEntity> findByLink(String link);

    @Transactional
    @Query("" +
        "UPDATE yellow_page" +
        "   SET active = FALSE" +
        " WHERE active")
    @Modifying
    void setAllInactive();

    @Transactional(readOnly = true)
    @Query("" +
        "SELECT link, name, owner_name, owner_castle, profession, mana, max_mana, active, last_active_time" +
        "  FROM yellow_page" +
        " WHERE link >= :link" +
        " ORDER BY link" +
        " LIMIT 1")
    Optional<YellowPageEntity> findFirstByLinkGreaterThanOrderByLink(String link);

    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Transactional(readOnly = true)
    @Query("" +
        "SELECT link" +
        "  FROM yellow_page" +
        " WHERE link < :link" +
        " ORDER BY link DESC" +
        " LIMIT 1")
    Optional<String> findTopByLinkBeforeOrderByLink(String link);

    @SuppressWarnings("SpringDataRepositoryMethodReturnTypeInspection")
    @Transactional(readOnly = true)
    @Query("" +
        "SELECT link" +
        "  FROM yellow_page" +
        " WHERE link > :link" +
        " ORDER BY link" +
        " LIMIT 1")
    Optional<String> findFirstByLinkAfterOrderByLink(String link);
}
