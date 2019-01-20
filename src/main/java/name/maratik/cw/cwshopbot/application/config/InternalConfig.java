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
package name.maratik.cw.cwshopbot.application.config;

import name.maratik.cw.cwshopbot.application.dao.AssetsDao;
import name.maratik.cw.cwshopbot.application.repository.Base;
import name.maratik.cw.cwshopbot.model.Castle;
import name.maratik.cw.cwshopbot.model.ForwardKey;
import name.maratik.cw.cwshopbot.model.Profession;
import name.maratik.cw.cwshopbot.model.cwasset.Assets;
import name.maratik.cw.cwshopbot.util.LRUCachingMap;
import name.maratik.spring.telegram.config.TelegramBotBuilder;
import name.maratik.spring.telegram.config.TelegramBotType;
import name.maratik.spring.telegram.util.Localizable;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.config.JdbcConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Configuration
@PropertySource("classpath:cwshopbot.properties")
@Log4j2
@EnableAspectJAutoProxy
@EnableLoadTimeWeaving
@EnableSpringConfigured
public class InternalConfig {
    public InternalConfig() {
        log.debug("Start config");
    }

    @Bean
    public TelegramBotType telegramBotType() {
        return TelegramBotType.LONG_POLLING;
    }

    @Bean
    public TelegramBotBuilder telegramBotBuilder(@Value("${name.maratik.cw.cwshopbot.username}") String username,
                                                 @Value("${name.maratik.cw.cwshopbot.token}") String token) {
        return new TelegramBotBuilder()
            .username(username)
            .token(token)
            .maxThreads(5);
    }

    @Bean
    @ForwardUser
    public Cache<ForwardKey, Long> forwardUserCache(Ticker ticker, Duration forwardStale) {
        return CacheBuilder.newBuilder()
            .ticker(ticker)
            .recordStats()
            .expireAfterWrite(forwardStale)
            .removalListener(notification -> log.debug(
                "Removed forward {} from cache due to {}, evicted = {}",
                notification::toString, notification::getCause, notification::wasEvicted
            ))
            .maximumSize(1000)
            .build();
    }

    @Bean
    public Duration forwardStale(@Value("${forwardStale}") String forwardStaleStr) {
        return Duration.parse(forwardStaleStr);
    }

    @Bean
    public LRUCachingMap<Object, JavaType> unifiedObjectMapperCache(Ticker ticker) {
        return new LRUCachingMap<>(1000, Duration.of(1, ChronoUnit.DAYS), ticker);
    }

    @Bean
    public TypeFactory typeFactory(LRUCachingMap<Object, JavaType> unifiedObjectMapperCache) {
        return TypeFactory.defaultInstance().withCache(unifiedObjectMapperCache);
    }

    @Bean
    public Assets assets(ResourceLoader resourceLoader, TypeFactory typeFactory) throws IOException {
        return new AssetsDao(resourceLoader.getResource("classpath:assets/resources.yaml"), typeFactory)
            .createAssets();
    }

    @Bean
    public Localizable loc() {
        return new Localizable();
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
                        new Jdk8Module(),
                        new JavaTimeModule(),
                        new ParameterNamesModule()
                    )
            );
        }
    }

    @Configuration
    @EnableJdbcRepositories(basePackageClasses = Base.class)
    @EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
    public static class JdbcRepositoryConfiguration extends JdbcConfiguration {
        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.setFetchSize(200);
            return jdbcTemplate;
        }

        @Bean
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
            return new NamedParameterJdbcTemplate(jdbcTemplate);
        }

        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
            return new TransactionTemplate(transactionManager);
        }

        @Bean
        public DataSource dataSource(@ConnectionUrl String connectionUrl,
                                     @Value("${cwshopbot.db.username}") String username,
                                     @Value("${cwshopbot.db.password}") String password) {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriver(new org.postgresql.Driver());
            dataSource.setUrl(connectionUrl);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setValidationQuery("SELECT 1");
            dataSource.setMaxWaitMillis(TimeUnit.SECONDS.toMillis(10));
            dataSource.setMaxTotal(5);
            dataSource.setTestWhileIdle(true);
            dataSource.setInitialSize(2);
            dataSource.setMinIdle(1);
            dataSource.setTimeBetweenEvictionRunsMillis(TimeUnit.SECONDS.toMillis(10));
            dataSource.setMaxIdle(2);
            dataSource.setSoftMinEvictableIdleTimeMillis(TimeUnit.MINUTES.toMillis(10));
            dataSource.setMaxConnLifetimeMillis(TimeUnit.MINUTES.toMillis(20));
            dataSource.setDefaultAutoCommit(false);
            return dataSource;
        }

        @Bean
        public SpringLiquibase liquibase(DataSource dataSource) {
            SpringLiquibase liquibase = new SpringLiquibase();
            liquibase.setDataSource(dataSource);
            liquibase.setChangeLog("classpath:/changesets/changelog.xml");
            return liquibase;
        }

        @Override
        @NonNull
        protected JdbcCustomConversions jdbcCustomConversions() {
            return new JdbcCustomConversions(ImmutableList.of(Castle.CONVERTER, Profession.CONVERTER));
        }
    }
}
