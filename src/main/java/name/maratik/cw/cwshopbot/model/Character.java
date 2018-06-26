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

import java.util.Objects;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class Character {
    private final long userId;
    private final Optional<String> shopCode;
    private final String charName;
    private final Castle castle;
    private final Optional<String> guildAbbrev;

    private Character(long userId, String shopCode, String charName, Castle castle, String guildAbbrev) {
        this.userId = userId;
        this.shopCode = Optional.ofNullable(shopCode);
        this.charName = Objects.requireNonNull(charName, "charName");
        this.castle = Objects.requireNonNull(castle, "castle");
        this.guildAbbrev = Optional.ofNullable(guildAbbrev);
    }

    public long getUserId() {
        return userId;
    }

    public Optional<String> getShopCode() {
        return shopCode;
    }

    public String getCharName() {
        return charName;
    }

    public Castle getCastle() {
        return castle;
    }

    public Optional<String> getGuildAbbrev() {
        return guildAbbrev;
    }

    @Override
    public String toString() {
        return "Character{" +
            "userId=" + userId +
            ", shopCode=" + shopCode +
            ", charName='" + charName + '\'' +
            ", castle=" + castle +
            ", guildAbbrev=" + guildAbbrev +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long userId;
        private String shopCode;
        private String charName;
        private Castle castle;
        private String guildAbbrev;

        public Builder setUserId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder setShopCode(String shopCode) {
            this.shopCode = shopCode;
            return this;
        }

        public Builder setCharName(String charName) {
            this.charName = charName;
            return this;
        }

        public Builder setCastle(Castle castle) {
            this.castle = castle;
            return this;
        }

        public Builder setGuildAbbrev(String guildAbbrev) {
            this.guildAbbrev = guildAbbrev;
            return this;
        }

        public Character build() {
            return new Character(userId, shopCode, charName, castle, guildAbbrev);
        }
    }
}
