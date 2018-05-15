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
package name.maratik.cw.eu.cwshopbot.mock;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.google.common.collect.ImmutableList;
import name.maratik.cw.eu.cwshopbot.application.Application;
import name.maratik.cw.eu.cwshopbot.application.config.InternalConfig;
import name.maratik.cw.eu.cwshopbot.application.config.TestDynamoDBConfig;
import name.maratik.cw.eu.cwshopbot.application.dao.ShopDao;
import name.maratik.cw.eu.cwshopbot.application.dao.ShopLineDao;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.TelegramBotsApi;

import java.util.Collections;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@RunWith(SpringRunner.class)
@TestPropertySource(properties = {
    "name.maratik.cw.eu.cwshopbot.username=testUsername",
    "name.maratik.cw.eu.cwshopbot.token=test:Token",
    "name.maratik.cw.eu.cwshopbot.admin=123456789",
    "name.maratik.cw.eu.cwshopbot.dev=123321",
    "name.maratik.cw.eu.cwshopbot.dev.username=dev_user_name",
    "name.maratik.cw.eu.cwshopbot.ban=1233211",
    "name.maratik.cw.eu.cwshopbot.accessKeyId=qqq",
    "name.maratik.cw.eu.cwshopbot.secretAccessKey=zzz",
    "name.maratik.cw.eu.cwshopbot.region=eee",
    "cwuserid=987654321"
})
@ContextConfiguration(classes = {
    InternalConfig.class,
    TestDynamoDBConfig.class
})
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWebMvc
@AutoConfigureCache
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(Configuration.class),
    basePackageClasses = Application.class
)
public abstract class MockedTelegramBotsApiTest {
    @MockBean
    protected TelegramBotsApi telegramBotsApi;

    @Autowired
    private DynamoDB client;

    @Before
    public void setUp() throws InterruptedException {
        createTables();
    }

    @After
    public void cleanUp() {
        clearTables();
    }

    private void clearTables() {
        client.listTables().forEach(table -> {
            table.delete();
            try {
                table.waitForDelete();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void createTables() throws InterruptedException {
        ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(5L, 5L);

        createShopTable(client, provisionedThroughput);
        createShopLineTable(client, provisionedThroughput);
    }

    private static void createShopTable(DynamoDB client, ProvisionedThroughput provisionedThroughput) throws InterruptedException {
        client.createTable(ShopDao.TABLE_NAME, Collections.singletonList(
            new KeySchemaElement("code", KeyType.HASH)
        ), Collections.singletonList(
            new AttributeDefinition("code", ScalarAttributeType.S)
        ), provisionedThroughput).waitForActive();
    }

    private static void createShopLineTable(DynamoDB client, ProvisionedThroughput provisionedThroughput) throws InterruptedException {
        client.createTable(ShopLineDao.TABLE_NAME, ImmutableList.of(
            new KeySchemaElement("shopCode", KeyType.HASH),
            new KeySchemaElement("itemCode", KeyType.RANGE)
        ), ImmutableList.of(
            new AttributeDefinition("shopCode", ScalarAttributeType.S),
            new AttributeDefinition("itemCode", ScalarAttributeType.S)
        ), provisionedThroughput).waitForActive();
    }
}
