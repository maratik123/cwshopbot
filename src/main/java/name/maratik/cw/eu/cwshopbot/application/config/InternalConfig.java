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
package name.maratik.cw.eu.cwshopbot.application.config;

import name.maratik.cw.eu.cwshopbot.application.dao.AssetsDao;
import name.maratik.cw.eu.cwshopbot.model.ForwardKey;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Assets;
import name.maratik.cw.eu.spring.config.TelegramBotBuilder;
import name.maratik.cw.eu.spring.config.TelegramBotType;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Configuration
@PropertySource("classpath:cwshopbot.properties")
public class InternalConfig {
    private static final Logger logger = LogManager.getLogger(InternalConfig.class);

    @Bean
    public TelegramBotType telegramBotType() {
        return TelegramBotType.LONG_POLLING;
    }

    @Bean
    public TelegramBotBuilder telegramBotBuilder(
        @Value("${name.maratik.cw.eu.cwshopbot.username}") String username,
        @Value("${name.maratik.cw.eu.cwshopbot.token}") String token
    ) {
        return new TelegramBotBuilder()
            .username(username)
            .token(token);
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    @ForwardUser
    public Cache<ForwardKey, Long> forwardUserCache(@Value("${forwardStaleSec}") int forwardStaleSec, Clock clock) {
        return CacheBuilder.newBuilder()
            .ticker(new CacheTicker(clock))
            .expireAfterWrite(forwardStaleSec, TimeUnit.SECONDS)
            .removalListener(notification -> logger.debug(
                "Removed forward {} from cache due to {}, evicted = {}",
                notification::toString, notification::getCause, notification::wasEvicted
            ))
            .maximumSize(1000)
            .build();
    }

    @Bean
    public Assets assets(ResourceLoader resourceLoader) throws IOException {
        return new AssetsDao(resourceLoader.getResource("classpath:assets/resources.yaml")).createAssets();
    }

    @Bean
    public DynamoDB dynamoDB(AmazonDynamoDB client) {
            return new DynamoDB(client);
        }

    private static class CacheTicker extends Ticker {
        private final Instant ZERO = Instant.ofEpochSecond(0);
        private final Clock clock;

        private CacheTicker(Clock clock) {
            this.clock = clock;
        }

        @Override
        public long read() {
            return ZERO.until(clock.instant(), ChronoUnit.NANOS);
        }
    }
}
