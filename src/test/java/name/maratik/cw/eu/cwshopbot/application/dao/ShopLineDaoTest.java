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

import name.maratik.cw.eu.cwshopbot.mock.MockedTest;
import name.maratik.cw.eu.cwshopbot.model.Shop;
import name.maratik.cw.eu.cwshopbot.model.ShopLine;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Assets;
import name.maratik.cw.eu.cwshopbot.model.cwasset.CraftableItem;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Item;
import name.maratik.cw.eu.cwshopbot.model.cwasset.WearableItem;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static name.maratik.cw.eu.cwshopbot.application.dao.ShopDaoTest.createShop;
import static name.maratik.cw.eu.cwshopbot.application.dao.ShopDaoTest.createShopBuilder;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@SuppressWarnings("ZeroLengthArrayAllocation")
public class ShopLineDaoTest extends MockedTest {
    @Autowired
    private ShopLineDao shopLineDao;
    @Autowired
    private Assets assets;

    private Item item;
    private CraftableItem craftableItem;
    private WearableItem wearableItem;

    @Before
    public void init() {
        item = assets.getAllItems().values().iterator().next();
        craftableItem = assets.getCraftableItems().values().stream()
            .filter(itm -> !itm.equals(item))
            .findAny().orElseThrow(AssertionError::new);
        wearableItem = assets.getWearableItems().values().stream()
            .filter(itm -> !itm.equals(item))
            .filter(itm -> !itm.equals(craftableItem))
            .findAny().orElseThrow(AssertionError::new);
    }

    @Test
    public void shouldPutLine() throws DaoException {
        ShopLine shopLine = createShopLine(item);
        Shop shop = createShop(shopLine);
        shopLineDao.putShopLines(shop);
        Shop result = shopLineDao.getShopLines(createShopBuilder(), shop.getShopCode());
        assertThat(result.getShopLines(), contains(samePropertyValuesAs(shopLine)));
    }

    @Test
    public void shouldPutMultipleLines() throws DaoException {
        List<ShopLine> shopLines = Stream.of(item, craftableItem, wearableItem)
            .map(ShopLineDaoTest::createShopLine)
            .collect(toImmutableList());
        Shop shop = createShop(shopLines.toArray(new ShopLine[0]));
        shopLineDao.putShopLines(shop);
        Shop result = shopLineDao.getShopLines(createShopBuilder(), shop.getShopCode());
        assertThat(result.getShopLines(), containsInAnyOrder(shopLines.stream()
            .map(Matchers::samePropertyValuesAs)
            .collect(toImmutableList())
        ));
    }

    @Test
    public void shouldUpdatePrice() throws DaoException {
        ShopLine shopLine = createShopLine(item, 1);
        Shop shop = createShop(shopLine);
        shopLineDao.putShopLines(shop);
        shopLine = createShopLine(item, 2);
        shop = createShop(shopLine);
        shopLineDao.putShopLines(shop);
        Shop result = shopLineDao.getShopLines(createShopBuilder(), shop.getShopCode());
        assertThat(result.getShopLines(), contains(samePropertyValuesAs(shopLine)));
    }

    @SuppressWarnings("WeakerAccess")
    public static ShopLine createShopLine(Item item) {
        return createShopLine(item, 2);
    }

    @SuppressWarnings("WeakerAccess")
    public static ShopLine createShopLine(Item item, int price) {
        return ShopLine.builder()
            .setItem(item)
            .setPrice(price)
            .build();
    }
}
