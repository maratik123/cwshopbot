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
package name.maratik.cw.cwshopbot.model.cwapi;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class Profile {
    private final int atk;
    private final CastleByEmoji castle;
    private final int def;
    private final int exp;
    private final int gold;
    private final int lvl;
    private final int pouches;
    private final String userName;

    public Profile(
        @JsonProperty("atk") int atk,
        @JsonProperty("castle") CastleByEmoji castle,
        @JsonProperty("def") int def,
        @JsonProperty("exp") int exp,
        @JsonProperty("gold") int gold,
        @JsonProperty("lvl") int lvl,
        @JsonProperty("pouches") int pouches,
        @JsonProperty("userName") String userName
    ) {
        this.atk = atk;
        this.castle = castle;
        this.def = def;
        this.exp = exp;
        this.gold = gold;
        this.lvl = lvl;
        this.pouches = pouches;
        this.userName = userName;
    }

    public int getAtk() {
        return atk;
    }

    public CastleByEmoji getCastle() {
        return castle;
    }

    public int getDef() {
        return def;
    }

    public int getExp() {
        return exp;
    }

    public int getGold() {
        return gold;
    }

    public int getLvl() {
        return lvl;
    }

    public int getPouches() {
        return pouches;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "Profile{" +
            "atk=" + atk +
            ", castle=" + castle +
            ", def=" + def +
            ", exp=" + exp +
            ", gold=" + gold +
            ", lvl=" + lvl +
            ", pouches=" + pouches +
            ", userName='" + userName + '\'' +
            '}';
    }
}
