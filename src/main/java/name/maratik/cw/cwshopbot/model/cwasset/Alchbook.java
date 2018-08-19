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
package name.maratik.cw.cwshopbot.model.cwasset;

import name.maratik.cw.cwshopbot.util.EnumWithCode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum Alchbook implements EnumWithCode, Book {
    ALCHBOOK_1("1", true),
    ALCHBOOK_2("2", true),
    ALCHBOOK_3("3", true);

    public static final String COMMAND_PREFIX = "/alchbook_";

    private final String code;
    private final boolean visible;
    private static final Map<String, Alchbook> cache = Util.createCache(values());

    Alchbook(String code, boolean visible) {
        this.code = code;
        this.visible = visible;
    }

    @JsonValue
    @Override
    public String getCode() {
        return code;
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public String getCommandPrefix() {
        return COMMAND_PREFIX;
    }

    @JsonCreator
    public static Optional<Alchbook> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }
}
