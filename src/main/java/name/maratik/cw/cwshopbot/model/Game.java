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

import name.maratik.cw.cwshopbot.parser.generated.HeroLexer;
import name.maratik.cw.cwshopbot.parser.generated.HeroLexerRu;
import name.maratik.cw.cwshopbot.util.EnumWithCode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum Game implements EnumWithCode {
    RU2("RU2") {
        @Override
        public Lexer createHeroLexer(CharStream messageCharStream) {
            return new HeroLexerRu(messageCharStream);
        }
    },
    EN("EN") {
        @Override
        public HeroLexer createHeroLexer(CharStream messageCharStream) {
            return new HeroLexer(messageCharStream);
        }
    };

    private final String code;
    private static final Map<String, Game> cache = Util.createCache(values());

    Game(String code) {
        this.code = code;
    }

    @JsonValue
    @Override
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static Optional<Game> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }

    public abstract Lexer createHeroLexer(CharStream messageCharStream);
}
