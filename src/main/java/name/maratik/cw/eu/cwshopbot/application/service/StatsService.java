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

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
public class StatsService {
    private final Instant startTime;
    private final Clock clock;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_INSTANT;
    private final AtomicLong gcCount = new AtomicLong(0L);
    private final AtomicLong gcTime = new AtomicLong(0L);

    public StatsService(Clock clock) {
        this.clock = clock;
        startTime = clock.instant();
    }

    public String getMessage() {
        Duration workTime = Duration.between(
            startTime.atOffset(ZoneOffset.UTC),
            clock.instant().atOffset(ZoneOffset.UTC)
        );
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        return "Application started in " + dateTimeFormatter.format(startTime) + '\n' +
            "Work time is " + workTime + '\n' +
            "Total memory is " + totalMemory + " bytes\n" +
            "Free memory is " + freeMemory + " bytes\n" +
            "Used memory is " + (totalMemory - freeMemory) + " bytes\n" +
            "GC count is " + this.gcCount + '\n' +
            "GC time is " + this.gcTime + " ms\n";
    }

    public void setGCCount(long gcCount) {
        this.gcCount.set(gcCount);
    }

    public void setGCTime(long gcTime) {
        this.gcTime.set(gcTime);
    }
}
