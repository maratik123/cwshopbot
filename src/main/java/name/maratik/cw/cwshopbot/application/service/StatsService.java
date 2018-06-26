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
package name.maratik.cw.cwshopbot.application.service;

import name.maratik.cw.cwshopbot.application.config.ForwardUser;
import name.maratik.cw.cwshopbot.util.LRUCachingMap;
import name.maratik.cw.cwshopbot.application.config.ForwardUser;
import name.maratik.cw.cwshopbot.model.ForwardKey;
import name.maratik.cw.cwshopbot.util.LRUCachingMap;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.cache.Cache;
import org.springframework.stereotype.Service;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
public class StatsService {
    private final OffsetDateTime startTime;
    private final Clock clock;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_INSTANT;
    private final LRUCachingMap<Object, JavaType> unifiedObjectMapperCache;
    private final Cache<ForwardKey, Long> forwardUserCache;

    public StatsService(Clock clock, LRUCachingMap<Object, JavaType> unifiedObjectMapperCache,
                        @ForwardUser Cache<ForwardKey, Long> forwardUserCache) {
        this.clock = clock;
        startTime = clock.instant().atOffset(ZoneOffset.UTC);
        this.unifiedObjectMapperCache = unifiedObjectMapperCache;
        this.forwardUserCache = forwardUserCache;
    }

    public String getStats() {
        Duration workTime = Duration.between(startTime, clock.instant().atOffset(ZoneOffset.UTC));
        GCStats gcStats = getGCStats();
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        return "Application started in " + dateTimeFormatter.format(startTime) + '\n' +
            "Work time is " + workTime + '\n' +
            "Memory heap usage is " + memoryMXBean.getHeapMemoryUsage() + '\n' +
            "Memory non-heap usage is " + memoryMXBean.getNonHeapMemoryUsage() + '\n' +
            "Objects pending finalization is " + memoryMXBean.getObjectPendingFinalizationCount() + '\n' +
            "GC count is " + gcStats.getCount() + '\n' +
            "GC time is " + gcStats.getTime() + " ms\n" +
            "Classes loaded is " + classLoadingMXBean.getLoadedClassCount() + '\n' +
            "Total classes loaded is " + classLoadingMXBean.getTotalLoadedClassCount() + '\n' +
            "Unloaded classes is " + classLoadingMXBean.getUnloadedClassCount() + '\n' +
            "System load average is " + ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
    }

    private static GCStats getGCStats() {
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
        return new GCStats(gcCount, gcTime);
    }

    public String getCacheStats() {
        return "Forward user cache: " + forwardUserCache.stats() + '\n' +
            "Unified object mapper cache: " + unifiedObjectMapperCache.stats();
    }

    private static class GCStats {
        private final long count;
        private final long time;

        private GCStats(long count, long time) {
            this.count = count;
            this.time = time;
        }

        private long getCount() {
            return count;
        }

        private long getTime() {
            return time;
        }
    }
}
