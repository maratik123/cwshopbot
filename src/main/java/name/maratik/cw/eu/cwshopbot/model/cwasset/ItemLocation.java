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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import name.maratik.cw.eu.cwshopbot.util.EnumWithCode;

import java.util.Map;
import java.util.Optional;

import static name.maratik.cw.eu.cwshopbot.util.Emoji.BOX;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.CHEST;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.HAMMERS;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.LABEL;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum ItemLocation implements EnumWithCode {
    STOCK("stock", BOX + "Resources"),
    CRAFTING("crafting", HAMMERS + "Crafting"),
    MISC("misc", CHEST + "Misc"),
    EQUIPMENT("equipment", LABEL + "Equipment");

    private final String code;
    private final String buttonText;
    private static final Map<String, ItemLocation> cache = Util.createCache(values());

    ItemLocation(String code, String buttonText) {
        this.code = code;
        this.buttonText = buttonText;
    }

    @Override
    @JsonValue
    public String getCode() {
        return code;
    }

    public String getButtonText() {
        return buttonText;
    }

    @JsonCreator
    public static Optional<ItemLocation> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }
}
