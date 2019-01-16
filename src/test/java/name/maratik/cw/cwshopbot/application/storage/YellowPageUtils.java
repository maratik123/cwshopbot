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
package name.maratik.cw.cwshopbot.application.storage;

import name.maratik.cw.cwshopbot.model.cwapi.CastleByEmoji;
import name.maratik.cw.cwshopbot.model.cwapi.ProfessionByEmoji;
import name.maratik.cw.cwshopbot.model.cwapi.YellowPage;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class YellowPageUtils {
    public static YellowPage.YellowPageBuilder createYellowPage() {
        return YellowPage.builder()
            .link("testLink")
            .name("testName")
            .ownerName("testOwnerName")
            .ownerCastle(CastleByEmoji.MOONLIGHT)
            .kind(ProfessionByEmoji.BLACKSMITH)
            .mana(100)
            .active(true)
            .offers(singletonList(
                YellowPage.Offer.builder()
                    .price(200)
                    .mana(300)
                    .item("testItem")
                    .active(true)
                    .build()
            ))
            .specialization(singletonMap("testSpecialization", 400));
    }
}
