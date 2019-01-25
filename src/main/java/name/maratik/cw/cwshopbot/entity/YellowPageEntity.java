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

import name.maratik.cw.cwshopbot.model.Castle;
import name.maratik.cw.cwshopbot.model.Profession;
import name.maratik.cw.cwshopbot.model.cwapi.YellowPage;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Value
@Builder(toBuilder = true)
@Table("yellow_page")
public class YellowPageEntity {
    @Id
    @NonNull
    private final String link;
    @NonNull
    private final String name;
    @NonNull
    private final String ownerName;
    @NonNull
    private final Castle ownerCastle;
    @NonNull
    private final Profession profession;
    private final int mana;
    private final int maxMana;
    private final boolean active;
    @NonNull
    private final LocalDateTime lastActiveTime;

    public static YellowPageEntity of(YellowPage yellowPage, LocalDateTime lastActiveTime) {
        return new YellowPageEntity(
            yellowPage.getLink(),
            yellowPage.getName(),
            yellowPage.getOwnerName(),
            yellowPage.getOwnerCastle().getCastle(),
            yellowPage.getKind().getProfession(),
            yellowPage.getMana(),
            yellowPage.getMana(),
            true,
            lastActiveTime
        );
    }

    @Value
    @Table("yellow_page")
    public static class Link {
        @NonNull
        private final String link;
    }
}
