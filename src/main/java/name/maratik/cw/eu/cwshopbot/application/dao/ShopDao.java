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
package name.maratik.cw.eu.cwshopbot.application.dao;

import name.maratik.cw.eu.cwshopbot.model.Shop;

import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Repository
public class ShopDao {
    private static final Logger logger = LogManager.getLogger(ShopDao.class);
    public static final String TABLE_NAME = "shop";
    public static final String USER_ID_INDEX = "userId-index";

    private final Table shopTable;

    public ShopDao(DynamoDB dynamoDB) {
        shopTable = dynamoDB.getTable(TABLE_NAME);
    }

    public Optional<Shop.Builder> getShopByCode(String code) throws DaoException {
        logger.debug("Get shop info for {}", code);
        try {
            return Optional.ofNullable(shopTable.getItem("code", code))
                .map(ShopDao::shopRowMapper);
        } catch (Exception e) {
            throw new DaoException("Getting shop '" + code + "' failed", e);
        }
    }

    public List<Shop.Builder> getAllShops() throws DaoException {
        logger.debug("Get all shops");
        try {
            return StreamSupport.stream(shopTable.scan().spliterator(), false)
                .map(ShopDao::shopRowMapper)
                .collect(toImmutableList());
        } catch (Exception e) {
            throw new DaoException("Getting all shops failed", e);
        }
    }

    private static Shop.Builder shopRowMapper(Item item) {
        return Shop.builder()
            .setShopCode(item.getString("code"))
            .setShopCommand(item.getString("command"))
            .setCharName(item.getString("charName"))
            .setShopName(item.getString("name"))
            .setMaxOffersCount(item.getInt("maxOffersCount"))
            .setShopNumber(item.getInt("shopNumber"));
    }

    public void putShop(Shop shop) throws DaoException {
        logger.debug("Putting {} to db", shop);
        try {
            PutItemOutcome outcome = shopTable.putItem(new Item().withPrimaryKey("code", shop.getShopCode())
                .withString("name", shop.getShopName())
                .withString("command", shop.getShopCommand())
                .withString("charName", shop.getCharName())
                .withInt("maxOffersCount", shop.getMaxOffersCount())
                .withInt("shopNumber", shop.getShopNumber())
            );
            logger.debug("Result is {}", outcome);
        } catch (Exception e) {
            throw new DaoException("Putting shop " + shop + " failed", e);
        }
    }

    public void deleteShop(String code) throws DaoException {
        logger.debug("Delete shop {} from db", code);
        try {
            DeleteItemOutcome outcome = shopTable.deleteItem("code", code);
            logger.debug("Result is {}", outcome);
        } catch (Exception e) {
            throw new DaoException("Deleting shop '" + code + "' failed", e);
        }
    }
}
