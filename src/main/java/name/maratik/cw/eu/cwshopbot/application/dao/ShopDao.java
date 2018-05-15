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

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import name.maratik.cw.eu.cwshopbot.model.Shop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Repository
public class ShopDao {
    private static final Logger logger = LogManager.getLogger(ShopDao.class);
    public static final String TABLE_NAME = "shop";

    private final Table shopTable;

    public ShopDao(DynamoDB dynamoDB) {
        shopTable = dynamoDB.getTable(TABLE_NAME);
    }

    public Optional<Shop.Builder> getShopByCode(String code) throws DaoException {
        logger.debug("Get shop info for {}", code);
        try {
            return Optional.ofNullable(shopTable.getItem("code", code))
                .map(item -> Shop.builder()
                    .setShopCode(code)
                    .setShopCommand(item.getString("command"))
                    .setCharName(item.getString("charName"))
                    .setShopName(item.getString("name"))
                    .setMaxOffersCount(item.getInt("maxOffersCount"))
                    .setShopNumber(item.getInt("shopNumber"))
                );
        } catch (Exception e) {
            throw new DaoException("Getting shop failed", e);
        }
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
            throw new DaoException("Putting shop failed", e);
        }
    }
}
