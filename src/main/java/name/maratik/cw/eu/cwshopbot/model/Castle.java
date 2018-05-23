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

import name.maratik.cw.eu.cwshopbot.util.Emoji;
import name.maratik.cw.eu.cwshopbot.util.EnumWithCode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Optional;

import static name.maratik.cw.eu.cwshopbot.util.Emoji.DEER;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.DRAGON;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.EAGLE;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.MOON;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.SHARK;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.WOLF;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum Castle implements EnumWithCode {
    MOONLIGHT("Moonlight", MOON),
    WOLFPACK("Wolfpack", WOLF),
    POTATO("Potato", Emoji.POTATO),
    SHARKTEETH("Sharkteeth", SHARK),
    HIGHNEST("Highnest", EAGLE),
    DEERHORN("Deerhorn", DEER),
    DRAGONSCALE("Dragonscale", DRAGON);

    private final String code;
    private final String gameName;
    private static final Map<String, Castle> cache = Util.createCache(values());

    Castle(String code, String emoji) {
        this.code = code;
        this.gameName = emoji + code;
    }

    @JsonValue
    @Override
    public String getCode() {
        return code;
    }

    public String getGameName() {
        return gameName;
    }

    @JsonCreator
    public static Optional<Castle> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }
}
