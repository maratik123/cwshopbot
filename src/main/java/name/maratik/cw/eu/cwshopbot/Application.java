//    cwshopbot
//    Copyright (C) 2018  Marat Bukharov.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package name.maratik.cw.eu.cwshopbot;

import name.maratik.cw.eu.spring.annotation.EnableTelegramBot;
import name.maratik.cw.eu.spring.config.TelegramBotBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@Configuration
@EnableTelegramBot
@PropertySource("file:${HOME}/cwshopbotconfig/auth.properties")
public class Application {
    private static final Logger logger = LogManager.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting main app");
        SpringApplication.run(Application.class, args);
        logger.info("Main app started. Exiting main thread");
    }

    @SuppressWarnings("MethodMayBeStatic")
    @Bean
    TelegramBotBuilder telegramBotBuilder(
        @Value("${name.maratik.cw.eu.cwshopbot.username}") String username,
        @Value("${name.maratik.cw.eu.cwshopbot.username}") String token
    ) {
        logger.debug("username = {}", username);
        return new TelegramBotBuilder()
            .username(username)
            .token(token);
    }
}
