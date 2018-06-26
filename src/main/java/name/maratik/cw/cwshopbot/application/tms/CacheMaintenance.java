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
package name.maratik.cw.cwshopbot.application.tms;

import name.maratik.cw.cwshopbot.util.LRUCachingMap;
import name.maratik.cw.cwshopbot.model.ForwardKey;
import name.maratik.cw.cwshopbot.util.LRUCachingMap;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.cache.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Component
public class CacheMaintenance {
    private static final Logger logger = LogManager.getLogger(CacheMaintenance.class);

    private final Cache<ForwardKey, Long> forwardUserCache;
    private final LRUCachingMap<Object, JavaType> unifiedObjectMapperCache;

    public CacheMaintenance(Cache<ForwardKey, Long> forwardUserCache, LRUCachingMap<Object, JavaType> unifiedObjectMapperCache) {
        this.forwardUserCache = forwardUserCache;
        this.unifiedObjectMapperCache = unifiedObjectMapperCache;
    }

    @Scheduled(fixedRateString = "PT1H")
    public void cleanupForwardUserCache() {
        try {
            forwardUserCache.cleanUp();
        } catch (Exception e) {
            logger.error("Failed with cleanupForwardUserCache", e);
        }
    }

    @Scheduled(fixedRateString = "P1D", initialDelayString = "PT30M")
    public void cleanupUnifiedObjectMapperCache() {
        try {
            unifiedObjectMapperCache.cleanUp();
        } catch (Exception e) {
            logger.error("Failed with cleanupUnifiedObjectMapperCache", e);
        }
    }
}
