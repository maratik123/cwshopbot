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

import com.google.common.cache.Cache;
import name.maratik.cw.eu.cwshopbot.model.ForwardKey;
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

    public CacheMaintenance(Cache<ForwardKey, Long> forwardUserCache) {
        this.forwardUserCache = forwardUserCache;
    }

    @Scheduled(fixedRate = 60 * 1000L)
    public void cleanupForwardUserCache() {
        try {
            logger.debug("Stat before cleanup: {}", forwardUserCache::stats);
            forwardUserCache.cleanUp();
            logger.debug("Stat after cleanup: {}", forwardUserCache::stats);
        } catch (Exception e) {
            logger.error("Failed with cleanupForwardUserCache", e);
        }
    }
}
