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

import name.maratik.cw.eu.cwshopbot.application.Application;
import name.maratik.cw.eu.cwshopbot.application.config.InternalConfig;
import name.maratik.cw.eu.cwshopbot.application.config.MocksConfig;
import name.maratik.cw.eu.cwshopbot.application.config.TestDynamoDBConfig;
import name.maratik.cw.eu.cwshopbot.application.dao.DaoException;
import name.maratik.cw.eu.cwshopbot.application.dao.ShopDao;
import name.maratik.cw.eu.cwshopbot.application.dao.ShopLineDao;
import name.maratik.cw.eu.cwshopbot.model.Shop;
import name.maratik.cw.eu.cwshopbot.model.ShopLine;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Item;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.Mockito.reset;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:test-application.properties")
@ContextConfiguration(classes = {
    InternalConfig.class,
    TestDynamoDBConfig.class,
    MocksConfig.class
})
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWebMvc
@AutoConfigureCache
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(Configuration.class),
    basePackageClasses = { Application.class, MockedTest.CreateTables.class }
)
public abstract class MockedTest {
    @Autowired
    private ShopDao shopDao;

    @Autowired
    private ShopLineDao shopLineDao;

    @Autowired
    private Map<Object, Consumer<Object>> mocks;

    @Before
    public void init() {
        setupLocale();
        resetMocks();
    }

    private static void setupLocale() {
        Locale.setDefault(new Locale("en"));
    }

    private void resetMocks() {
        mocks.forEach((mock, resetAction) -> {
            reset(mock);
            resetAction.accept(mock);
        });
    }

    @After
    public void cleanUp() throws DaoException {
        clearTables();
    }

    private void clearTables() throws DaoException {
        clearShopTable();
        clearShopLineTable();
    }

    private void clearShopTable() throws DaoException {
        shopDao.getAllShops().stream()
            .map(Shop.Builder::build)
            .map(Shop::getShopCode)
            .forEach(code -> {
                try {
                    shopDao.deleteShop(code);
                } catch (DaoException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    private void clearShopLineTable() throws DaoException {
        shopLineDao.getAllShopLines().forEach((shopCode, shopLine) ->
            shopLine.stream()
                .map(ShopLine::getItem)
                .map(Item::getId)
                .forEach(itemId -> {
                    try {
                        shopLineDao.deleteLine(shopCode, itemId);
                    } catch (DaoException e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }

    @Component
    public static class CreateTables {
        @Autowired
        private DynamoDB client;

        @PostConstruct
        public void createTables() throws InterruptedException {
            ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(5L, 5L);

            createShopTable(provisionedThroughput);
            createShopLineTable(provisionedThroughput);
        }

        private void createShopTable(ProvisionedThroughput provisionedThroughput) throws InterruptedException {
            CreateTableRequest request = new CreateTableRequest()
                .withTableName(ShopDao.TABLE_NAME)
                .withKeySchema(
                    new KeySchemaElement("code", KeyType.HASH)
                ).withAttributeDefinitions(
                    new AttributeDefinition("code", ScalarAttributeType.S),
                    new AttributeDefinition("userId", ScalarAttributeType.S)
                ).withProvisionedThroughput(provisionedThroughput)
                .withGlobalSecondaryIndexes(new GlobalSecondaryIndex()
                    .withIndexName(ShopDao.USER_ID_INDEX)
                    .withKeySchema(
                        new KeySchemaElement("userId", KeyType.HASH)
                    ).withProjection(new Projection()
                        .withProjectionType(ProjectionType.ALL)
                    ).withProvisionedThroughput(provisionedThroughput)
                );
            client.createTable(request).waitForActive();
        }

        private void createShopLineTable(ProvisionedThroughput provisionedThroughput) throws InterruptedException {
            CreateTableRequest request = new CreateTableRequest()
                .withTableName(ShopLineDao.TABLE_NAME)
                .withKeySchema(
                    new KeySchemaElement("shopCode", KeyType.HASH),
                    new KeySchemaElement("itemCode", KeyType.RANGE)
                ).withAttributeDefinitions(
                    new AttributeDefinition("shopCode", ScalarAttributeType.S),
                    new AttributeDefinition("itemCode", ScalarAttributeType.S)
                ).withProvisionedThroughput(provisionedThroughput)
                .withGlobalSecondaryIndexes(new GlobalSecondaryIndex()
                    .withIndexName(ShopLineDao.ITEM_CODE_SHOP_CODE_INDEX)
                    .withKeySchema(
                        new KeySchemaElement("itemCode", KeyType.HASH),
                        new KeySchemaElement("shopCode", KeyType.RANGE)
                    )
                    .withProjection(new Projection()
                        .withProjectionType(ProjectionType.ALL)
                    ).withProvisionedThroughput(provisionedThroughput)
                );
            client.createTable(request).waitForActive();
        }
    }
}
