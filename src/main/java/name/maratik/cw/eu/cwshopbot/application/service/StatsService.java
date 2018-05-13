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
package name.maratik.cw.eu.cwshopbot.application.service;

import com.google.common.cache.Cache;
import name.maratik.cw.eu.cwshopbot.application.config.ForwardUser;
import name.maratik.cw.eu.cwshopbot.model.ForwardKey;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
public class StatsService {
    private final Instant startTime;
    private final Cache<ForwardKey, Long> forwardUserCache;
    private final Clock clock;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_INSTANT;


    public StatsService(Clock clock, @ForwardUser Cache<ForwardKey, Long> forwardUserCache) {
        this.clock = clock;
        startTime = clock.instant();
        this.forwardUserCache = forwardUserCache;
    }

    public String getMessage() {
        Duration workTime = Duration.between(
            startTime.atOffset(ZoneOffset.UTC),
            clock.instant().atOffset(ZoneOffset.UTC)
        );
        return "Application started in " + dateTimeFormatter.format(startTime) + '\n' +
            "Work time is " + workTime + '\n' +
            "Total memory is " + Runtime.getRuntime().totalMemory() + '\n' +
            "Forward user cache stats: " + forwardUserCache.stats() + '\n';
    }
}
