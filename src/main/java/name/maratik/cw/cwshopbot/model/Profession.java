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
package name.maratik.cw.cwshopbot.model;

import name.maratik.cw.cwshopbot.util.EnumWithCode;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.Optional;

import static name.maratik.cw.cwshopbot.util.Emoji.ALEMBIC;
import static name.maratik.cw.cwshopbot.util.Emoji.HAMMERS;
import static name.maratik.cw.cwshopbot.util.Emoji.SWORDS;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@RequiredArgsConstructor
public enum Profession implements EnumWithCode {
    BLACKSMITH("Blacksmith", HAMMERS),
    ALCHEMIST("Alchemist", ALEMBIC),
    KNIGHT("Knight", SWORDS);

    @Getter(onMethod_ = {@JsonValue})
    private final String code;
    @Getter
    private final String emoji;
    private static final Map<String, Profession> cache = Util.createCache(values());

    public static Optional<Profession> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }

    @SuppressWarnings("Convert2Lambda")
    public static final Converter<String, Profession> CONVERTER = new Converter<String, Profession>() {
        @Override
        public Profession convert(@NonNull String source) {
            return findByCode(source)
                .orElseThrow(() -> new IllegalArgumentException("Unknown profession: " + source));
        }
    };
}
