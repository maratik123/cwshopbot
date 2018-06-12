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

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import maratik.name.spring.telegram.annotation.EnableTelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Configuration
@EnableTelegramBot
public class ExternalConfig {

    @Configuration
    public static class AWSConfiguration {
        @Value("${name.maratik.cw.eu.cwshopbot.accessKeyId}")
        private String accessKey;
        @Value("${name.maratik.cw.eu.cwshopbot.secretAccessKey}")
        private String secretKey;
        @Value("${name.maratik.cw.eu.cwshopbot.region}")
        private String regionName;

        @Bean
        public AWSCredentials awsCredentials() {
            return new BasicAWSCredentials(accessKey, secretKey);
        }

        @Bean
        public AWSCredentialsProvider awsCredentialsProvider() {
            return new AWSStaticCredentialsProvider(awsCredentials());
        }

        @Bean
        public Regions regions() {
            return Regions.fromName(regionName);
        }

        @Bean
        public AmazonDynamoDB amazonDynamoDB() {
            return AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_1)
                .withCredentials(awsCredentialsProvider())
                .build();
        }
    }
}
