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

import org.springframework.data.jdbc.repository.query.Modifying;

import java.util.List;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public interface CustomizedYellowPageOfferRepository {
    @Modifying
    void save(YellowPageOfferEntity yellowPageOfferEntity);

    @Modifying
    void saveAll(List<YellowPageOfferEntity> yellowPageOfferEntities);
}
