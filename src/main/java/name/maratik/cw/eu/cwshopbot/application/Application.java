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
package name.maratik.cw.eu.cwshopbot.application;

import name.maratik.cw.eu.cwshopbot.application.config.ExternalConfig;
import name.maratik.cw.eu.cwshopbot.application.config.InternalConfig;
import name.maratik.cw.eu.cwshopbot.application.config.TmsConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@Configuration
@PropertySource(value = "file:${HOME}/cwshopbotconfig/auth.properties", ignoreResourceNotFound = true)
@Import({
    InternalConfig.class,
    ExternalConfig.class,
    TmsConfig.class
})
@ComponentScan(excludeFilters = @ComponentScan.Filter(Configuration.class))
public class Application {

    public static void main(String[] args) {
        Logger logger = LogManager.getLogger(Application.class);
        logger.info("Starting main app");
        SpringApplication.run(Application.class, args);
        logger.info("Main app started. Exiting main thread");
    }
}
