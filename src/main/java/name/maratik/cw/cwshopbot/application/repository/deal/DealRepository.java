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

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Transactional
public interface DealRepository extends Repository<DealEntity, Long>, CustomizedDealRepository {
    @Transactional(readOnly = true)
    @Query("" +
        "SELECT id, seller_id, seller_name, seller_castle, buyer_id, buyer_name, buyer_castle, item, qty, price, creation_time" +
        "  FROM deal" +
        " WHERE id = :id")
    Optional<DealEntity> findById(long id);
}
