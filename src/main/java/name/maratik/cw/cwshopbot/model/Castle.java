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
package name.maratik.cw.cwshopbot.model;

import name.maratik.cw.cwshopbot.util.Emoji;
import name.maratik.cw.cwshopbot.util.EnumWithCode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static name.maratik.cw.cwshopbot.util.Emoji.AUBERGINE;
import static name.maratik.cw.cwshopbot.util.Emoji.BAT;
import static name.maratik.cw.cwshopbot.util.Emoji.BLACK_HEART;
import static name.maratik.cw.cwshopbot.util.Emoji.DEER;
import static name.maratik.cw.cwshopbot.util.Emoji.DRAGON;
import static name.maratik.cw.cwshopbot.util.Emoji.EAGLE;
import static name.maratik.cw.cwshopbot.util.Emoji.MAPLE_LEAF;
import static name.maratik.cw.cwshopbot.util.Emoji.MOON;
import static name.maratik.cw.cwshopbot.util.Emoji.ROSE;
import static name.maratik.cw.cwshopbot.util.Emoji.SHAMROCK;
import static name.maratik.cw.cwshopbot.util.Emoji.SHARK;
import static name.maratik.cw.cwshopbot.util.Emoji.TURTLE;
import static name.maratik.cw.cwshopbot.util.Emoji.WOLF;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum Castle implements EnumWithCode {
    MOONLIGHT("Moonlight", MOON, Game.EN),
    WOLFPACK("Wolfpack", WOLF, Game.EN),
    POTATO("Potato", Emoji.POTATO, Game.EN),
    SHARKTEETH("Sharkteeth", SHARK, Game.EN),
    HIGHNEST("Highnest", EAGLE, Game.EN),
    DEERHORN("Deerhorn", DEER, Game.EN),
    DRAGONSCALE("Dragonscale", DRAGON, Game.EN),
    TORTUGA("Тортуга", TURTLE, Game.RU2),
    NIGHT_CASTLE("Ночной Замок", BAT, Game.RU2),
    AMBER("Амбер", MAPLE_LEAF, Game.RU2),
    STRONGHOLD("Оплот", SHAMROCK, Game.RU2),
    CLIFF("Скала", BLACK_HEART, Game.RU2),
    DAWN_CASTLE("Замок Рассвета", ROSE, Game.RU2),
    FARM("Ферма", AUBERGINE, Game.RU2),
    URN("Urn", Emoji.URN, Game.EN, Game.RU2);

    private final String code;
    private final String castleName;
    private final String emoji;
    private final Set<Game> games;
    private static final Map<String, Castle> cache = Util.createCache(values());

    Castle(String code, String emoji, Game game, Game... otherGames) {
        this.code = code;
        this.castleName = emoji + code;
        this.emoji = emoji;
        this.games = Sets.immutableEnumSet(game, otherGames);
    }

    @JsonValue
    @Override
    public String getCode() {
        return code;
    }

    public String getCastleName() {
        return castleName;
    }

    public String getEmoji() {
        return emoji;
    }

    public Set<Game> getGames() {
        return games;
    }

    @JsonCreator
    public static Optional<Castle> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }
}
