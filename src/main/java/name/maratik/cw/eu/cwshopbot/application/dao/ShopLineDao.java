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
import name.maratik.cw.eu.cwshopbot.model.ShopLine;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Assets;
import name.maratik.cw.eu.cwshopbot.model.parser.ParsedShopEdit;
import name.maratik.cw.eu.cwshopbot.model.parser.ParsedShopInfo;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Repository
public class ShopLineDao {
    private static final Logger logger = LogManager.getLogger(ShopLineDao.class);
    public static final String TABLE_NAME = "shopLine";

    private final Table shopLineTable;
    private final Assets assets;

    public ShopLineDao(DynamoDB dynamoDB, Assets assets) {
        shopLineTable = dynamoDB.getTable(TABLE_NAME);
        this.assets = assets;
    }

    public Shop getShopLines(Shop.Builder shopBuilder, String shopCode) {
        shopLineTable.query("shopCode", shopCode)
            .forEach(item -> shopBuilder.addShopLine(ShopLine.builder()
                .setItem(assets.getAllItems().get(item.getString("itemCode")))
                .setPrice(item.getInt("price"))
                .build()
            ));
        return shopBuilder.build();
    }

    public void putShopLines(ParsedShopInfo parsedShopInfo) throws DaoException {
        try {
            parsedShopInfo.getShopLines().stream()
                .map(ShopLine::of)
                .forEach(shopLine -> putShopLine(parsedShopInfo.getShopCode(), shopLine));
        } catch (Exception e) {
            throw new DaoException("Putting shopLine failed", e);
        }
    }

    public void putShopLines(ParsedShopEdit parsedShopEdit) throws DaoException {
        try {
            parsedShopEdit.getShopLines().stream()
                .map(ShopLine::of)
                .forEach(shopLine -> putShopLine(parsedShopEdit.getShopCode(), shopLine));
        } catch (Exception e) {
            throw new DaoException("Putting shopLine failed", e);
        }
    }

    public void putShopLines(Shop shop) throws DaoException {
        try {
            shop.getShopLines().forEach(shopLine -> putShopLine(shop.getShopCode(), shopLine));
        } catch (Exception e) {
            throw new DaoException("Putting shopLine failed", e);
        }
    }

    private void putShopLine(String shopCode, ShopLine shopLine) {
        logger.debug("Putting {},{} to db", shopCode, shopLine);
        PutItemOutcome outcome = shopLineTable.putItem(new Item()
            .withPrimaryKey(
                "shopCode", shopCode,
                "itemCode", shopLine.getItem().getId()
            ).withInt("price", shopLine.getPrice())
        );
        logger.debug("Result is {}", outcome);
    }
}
