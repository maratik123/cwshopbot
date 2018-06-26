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
package name.maratik.cw.cwshopbot.model.parser;

import name.maratik.cw.cwshopbot.model.Castle;
import name.maratik.cw.cwshopbot.model.Castle;

import java.util.Objects;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ParsedHero {
    private final Castle castle;
    private final String charName;
    private final Optional<String> guildAbbrev;

    private ParsedHero(Castle castle, String charName, String guildAbbrev) {
        this.castle = Objects.requireNonNull(castle, "castle");
        this.charName = Objects.requireNonNull(charName, "charName");
        this.guildAbbrev = Optional.ofNullable(guildAbbrev);
    }

    public Castle getCastle() {
        return castle;
    }

    public String getCharName() {
        return charName;
    }

    public Optional<String> getGuildAbbrev() {
        return guildAbbrev;
    }

    @Override
    public String toString() {
        return "ParsedHero{" +
            "castle=" + castle +
            ", charName='" + charName + '\'' +
            ", guildAbbrev=" + guildAbbrev +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Castle castle;
        private String charName;
        private String guildAbbrev;

        public Builder setCastle(Castle castle) {
            this.castle = castle;
            return this;
        }

        public Builder setCharName(String charName) {
            this.charName = charName;
            return this;
        }

        public Builder setGuildAbbrev(String guildAbbrev) {
            this.guildAbbrev = guildAbbrev;
            return this;
        }

        public ParsedHero build() {
            return new ParsedHero(castle, charName, guildAbbrev);
        }
    }
}
