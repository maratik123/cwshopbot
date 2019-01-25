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
package name.maratik.cw.cwshopbot.entity;

import name.maratik.cw.cwshopbot.model.cwapi.YellowPage;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Value
@Builder(toBuilder = true)
@Table("yellow_page_offer")
public class YellowPageOfferEntity {
    @NonNull
    private final String yellowPage;
    @NonNull
    private final String item;
    private final int price;
    private final int mana;
    private final boolean active;
    @NonNull
    private final LocalDateTime lastActiveTime;

    public static YellowPageOfferEntity of(String yellowPage, YellowPage.Offer yellowPageOffer,
                                           LocalDateTime lastActiveTime) {
        return new YellowPageOfferEntity(
            yellowPage,
            yellowPageOffer.getItem(),
            yellowPageOffer.getPrice(),
            yellowPageOffer.getMana(),
            true,
            lastActiveTime
        );
    }

    @Value
    @Table("yellow_page_offer")
    public static class Content {
        @NonNull
        private final String item;
        private final int price;
        private final int mana;
        private final boolean active;
        @NonNull
        private final LocalDateTime lastActiveTime;
    }
}
