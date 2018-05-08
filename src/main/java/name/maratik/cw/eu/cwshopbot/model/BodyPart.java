//    cwshopbot
//    Copyright (C) 2018  Marat Bukharov.
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
package name.maratik.cw.eu.cwshopbot.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import name.maratik.cw.eu.cwshopbot.util.EnumWithCode;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum BodyPart implements EnumWithCode {
    BODY("body", ItemClass.ARMOR),
    HANDS("hands", ItemClass.ARMOR),
    LEGS("legs", ItemClass.ARMOR),
    HEAD("head", ItemClass.ARMOR),
    PRIMARY_HAND("primary hand", ItemClass.PRIMARY_WEAPON),
    SECONDARY_HAND("secondary hand", ItemClass.SECONDARY_WEAPON);

    private final String code;
    private final ItemClass itemClass;
    private static final Map<String, BodyPart> cache = Util.createCache(values());

    BodyPart(String code, ItemClass itemClass) {
        this.code = code;
        this.itemClass = itemClass;
    }

    @Override
    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static Optional<BodyPart> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }

    public ItemClass getItemClass() {
        return itemClass;
    }
}
