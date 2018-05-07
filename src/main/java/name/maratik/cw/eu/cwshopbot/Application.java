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
package name.maratik.cw.eu.cwshopbot;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import name.maratik.cw.eu.cwshopbot.config.ForwardUser;
import name.maratik.cw.eu.cwshopbot.model.ForwardKey;
import name.maratik.cw.eu.spring.annotation.EnableTelegramBot;
import name.maratik.cw.eu.spring.config.TelegramBotBuilder;
import name.maratik.cw.eu.spring.config.TelegramBotType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Configuration
@EnableTelegramBot
@EnableScheduling
@PropertySource({
    "file:${HOME}/cwshopbotconfig/auth.properties",
    "classpath:cwshopbot.properties"
})
public class Application {
    private static final Logger logger = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting main app");
        SpringApplication.run(Application.class, args);
        logger.info("Main app started. Exiting main thread");
    }

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
        Instant zero = Instant.ofEpochSecond(0);
        return CacheBuilder.newBuilder()
            .ticker(new Ticker() {
                @Override
                public long read() {
                    return zero.until(clock.instant(), ChronoUnit.NANOS);
                }
            })
            .expireAfterWrite(forwardStaleSec, TimeUnit.SECONDS)
            .removalListener(notification -> logger.debug(
                "Removed forward {} from forward user cache due to {}, evicted = {}",
                notification::toString, notification::getCause, notification::wasEvicted
            ))
            .maximumSize(1000)
            .recordStats()
            .build();
    }
}
