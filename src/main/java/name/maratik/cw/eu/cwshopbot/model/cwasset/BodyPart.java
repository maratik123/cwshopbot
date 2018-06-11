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
package name.maratik.cw.eu.cwshopbot.model.cwasset;

import name.maratik.cw.eu.cwshopbot.util.EnumWithCode;
import name.maratik.cw.eu.cwshopbot.util.LocalizableValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum BodyPart implements EnumWithCode, LocalizableValue {
    BODY("body", ItemClass.ARMOR, "bp.BODY"),
    HANDS("hands", ItemClass.ARMOR, "bp.HANDS"),
    LEGS("legs", ItemClass.ARMOR, "bp.LEGS"),
    HEAD("head", ItemClass.ARMOR, "bp.HEAD"),
    PRIMARY_HAND("primary hand", ItemClass.PRIMARY_WEAPON, "bp.HAND.PRIMARY"),
    SECONDARY_HAND("secondary hand", ItemClass.SECONDARY_WEAPON, "bp.HAND.SECONDARY");

    private final String code;
    private final ItemClass itemClass;
    private final String translationTag;
    private static final Map<String, BodyPart> cache = Util.createCache(values());

    BodyPart(String code, ItemClass itemClass, String translationTag) {
        this.code = code;
        this.itemClass = itemClass;
        this.translationTag = translationTag;
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

    @Override
    public String getTranslationTag() {
        return translationTag;
    }
}
