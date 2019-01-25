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

import name.maratik.cw.cwshopbot.model.cwapi.Specialization;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Value
@Builder(toBuilder = true)
@Table("yellow_page_specialization")
public class YellowPageSpecializationEntity {
    @NonNull
    private final String yellowPage;
    @NonNull
    private final Specialization specialization;
    private final int value;

    @Value
    @Table("yellow_page_specialization")
    public static class Content {
        @NonNull
        private final Specialization specialization;
        private final int value;
    }
}
