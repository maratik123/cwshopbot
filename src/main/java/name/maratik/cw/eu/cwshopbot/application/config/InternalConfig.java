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
import name.maratik.cw.eu.cwshopbot.util.LRUCachingMap;
import name.maratik.cw.eu.spring.config.TelegramBotBuilder;
import name.maratik.cw.eu.spring.config.TelegramBotType;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Configuration
@PropertySource("classpath:cwshopbot.properties")
public class InternalConfig {
    private final Logger logger = LogManager.getLogger(InternalConfig.class);

    @Value("${name.maratik.cw.eu.cwshopbot.username}")
    private String username;
    @Value("${name.maratik.cw.eu.cwshopbot.token}")
    private String token;
    @Value("${forwardStale}")
    private String forwardStaleStr;

    @Bean
    public TelegramBotType telegramBotType() {
        return TelegramBotType.LONG_POLLING;
    }

    @Bean
    public TelegramBotBuilder telegramBotBuilder(
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
    public Cache<ForwardKey, Long> forwardUserCache() {
        return CacheBuilder.newBuilder()
            .ticker(ticker())
            .recordStats()
            .expireAfterWrite(forwardStale())
            .removalListener(notification -> logger.debug(
                "Removed forward {} from cache due to {}, evicted = {}",
                notification::toString, notification::getCause, notification::wasEvicted
            ))
            .maximumSize(1000)
            .build();
    }

    @Bean
    public Duration forwardStale() {
        return Duration.parse(forwardStaleStr);
    }

    @Bean
    public Ticker ticker() {
        return new CacheTicker(clock());
    }

    @Bean
    public LRUCachingMap<Object, JavaType> unifiedObjectMapperCache() {
        return new LRUCachingMap<>(1000, Duration.of(1, ChronoUnit.DAYS), ticker());
    }

    @Bean
    public TypeFactory typeFactory() {
        return TypeFactory.defaultInstance().withCache(unifiedObjectMapperCache());
    }

    @Bean
    public Assets assets(ResourceLoader resourceLoader) throws IOException {
        return new AssetsDao(resourceLoader.getResource("classpath:assets/resources.yaml"), typeFactory())
            .createAssets();
    }

    @Bean
    public DynamoDB dynamoDB(AmazonDynamoDB client) {
        return new DynamoDB(client);
    }

    @Configuration
    public static class RabbitConfiguration {
        @Bean
        public MessageConverter messageConverter(TypeFactory typeFactory) {
            return new Jackson2JsonMessageConverter(
                new ObjectMapper()
                    .setTypeFactory(typeFactory)
                    .registerModules(
                        new AfterburnerModule(),
                        new Jdk8Module()
                    )
            );
        }
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
