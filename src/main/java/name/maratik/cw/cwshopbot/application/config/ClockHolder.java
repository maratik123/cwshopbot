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
package name.maratik.cw.cwshopbot.application.config;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public abstract class ClockHolder extends Clock implements Supplier<Clock> {
    @Override
    public long millis() {
        return get().millis();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + get().toString() + ')';
    }

    @Override
    public ZoneId getZone() {
        return get().getZone();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return get().withZone(zone);
    }

    @Override
    public Instant instant() {
        return get().instant();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ClockHolder)) {
            return false;
        }
        ClockHolder other = (ClockHolder) obj;
        return Objects.equals(get(), other.get());
    }

    @Override
    public int hashCode() {
        if (get() == null) {
            return 1;
        }
        return 1 + get().hashCode();
    }
}
