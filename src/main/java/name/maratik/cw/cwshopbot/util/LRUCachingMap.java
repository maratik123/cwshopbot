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
package name.maratik.cw.cwshopbot.util;

import com.fasterxml.jackson.databind.util.LRUMap;
import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Log4j2
public class LRUCachingMap<K, V> extends LRUMap<K, V> {
    private static final long serialVersionUID = 1378663091316250420L;

    @SuppressWarnings("InstanceVariableMayNotBeInitializedByReadObject")
    private final transient Cache<K, V> cache;
    @SuppressWarnings("InstanceVariableMayNotBeInitializedByReadObject")
    private final transient ConcurrentMap<K, V> cacheMap;

    public LRUCachingMap(int maxEntries, Duration expireDuration, Ticker ticker) {
        super(0, maxEntries);
        _jdkSerializeMaxEntries = maxEntries;
        _map.clear();
        cache = CacheBuilder.newBuilder()
            .ticker(ticker)
            .recordStats()
            .maximumSize(maxEntries)
            .expireAfterAccess(expireDuration)
            .removalListener(notification -> log.debug(
                "Removed object {} from cache due to {}, evicted = {}",
                notification::toString, notification::getCause, notification::wasEvicted
            ))
            .build();
        cacheMap = cache.asMap();
    }

    @Override
    public V put(K key, V value) {
        return cacheMap.put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return cacheMap.putIfAbsent(key, value);
    }

    @Override
    public V get(Object key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public int size() {
        return (int) cache.size();
    }

    public void cleanUp() {
        cache.cleanUp();
    }

    private void readObject(ObjectInputStream in) throws IOException {
        _jdkSerializeMaxEntries = in.readInt();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(_jdkSerializeMaxEntries);
    }

    @Override
    protected Object readResolve() {
        return new LRUCachingMap<>(_jdkSerializeMaxEntries, Duration.of(1, ChronoUnit.DAYS), Ticker.systemTicker());
    }

    public CacheStats stats() {
        return cache.stats();
    }
}
