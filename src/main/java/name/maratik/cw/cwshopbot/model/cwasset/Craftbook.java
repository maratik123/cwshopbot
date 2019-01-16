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
package name.maratik.cw.cwshopbot.model.cwasset;

import name.maratik.cw.cwshopbot.util.EnumWithCode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@RequiredArgsConstructor
public enum Craftbook implements EnumWithCode {
    CRAFTBOOK_1("1", true),
    CRAFTBOOK_2("2", true),
    CRAFTBOOK_3("3", true),
    CRAFTBOOK_4("4", true),
    CRAFTBOOK_5("5", true),
    CRAFTBOOK_X("x", false);

    @Getter(onMethod_ = {@JsonValue})
    private final String code;
    @Getter
    private final boolean visible;
    private static final Map<String, Craftbook> cache = Util.createCache(values());

    @JsonCreator
    public static Optional<Craftbook> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }
}
