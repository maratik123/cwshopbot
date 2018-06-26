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

import name.maratik.cw.cwshopbot.model.Profession;
import name.maratik.cw.cwshopbot.util.EnumWithCode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum ProfessionByEmoji implements EnumWithCode {
    BLACKSMITH(Profession.BLACKSMITH),
    ALCHEMIST(Profession.ALCHEMIST);

    private final Profession profession;
    private static final Map<String, ProfessionByEmoji> cache = Util.createCache(values());

    ProfessionByEmoji(Profession profession) {
        this.profession = profession;
    }

    public Profession getProfession() {
        return profession;
    }

    @JsonValue
    @Override
    public String getCode() {
        return profession.getEmoji();
    }

    @JsonCreator
    public static Optional<ProfessionByEmoji> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }
}
