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

import name.maratik.cw.cwshopbot.entity.YellowPageOfferEntity;
import name.maratik.cw.cwshopbot.entity.YellowPageSpecializationEntity;
import name.maratik.cw.cwshopbot.entity.YellowPageEntity;
import name.maratik.cw.cwshopbot.model.Castle;
import name.maratik.cw.cwshopbot.model.Profession;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class YellowPageEntityUtils {

    public static final String TEST_LINK = "testLink";

    public static YellowPageEntity.YellowPageEntityBuilder createYellowPageEntity() {
        return YellowPageEntity.builder()
            .link(TEST_LINK)
            .name("testShopName")
            .ownerName("testOwnerName")
            .ownerCastle(Castle.MOONLIGHT)
            .mana(100)
            .profession(Profession.BLACKSMITH)
            .active(true);
    }

    public static YellowPageOfferEntity.YellowPageOfferEntityBuilder createYellowPageOfferEntity() {
        return YellowPageOfferEntity.builder()
            .yellowPage(TEST_LINK)
            .item("testItem")
            .price(100)
            .mana(200)
            .active(true);
    }

    public static YellowPageSpecializationEntity.YellowPageSpecializationEntityBuilder createYellowPageSpecializationEntity() {
        return YellowPageSpecializationEntity.builder()
            .yellowPage(TEST_LINK)
            .specialization("testSpecialization")
            .value(100);
    }
}
