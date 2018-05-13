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
package name.maratik.cw.eu.cwshopbot.application.tms;

import name.maratik.cw.eu.cwshopbot.application.service.StatsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Component
public class GCStats {
    private final StatsService statsService;

    public GCStats(StatsService statsService) {
        this.statsService = statsService;
    }

    @Scheduled(fixedRate = 60 * 1000L)
    public void readStats() {
        long gcCount = 0;
        long gcTime = 0;
        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            long count = garbageCollectorMXBean.getCollectionCount();
            if (count >= 0) {
                gcCount += count;
            }
            long time = garbageCollectorMXBean.getCollectionTime();
            if (time >= 0) {
                gcTime += time;
            }
        }
        statsService.setGCCount(gcCount);
        statsService.setGCTime(gcTime);
    }
}
