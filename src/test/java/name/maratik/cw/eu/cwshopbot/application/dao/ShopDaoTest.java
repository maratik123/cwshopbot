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

import name.maratik.cw.eu.cwshopbot.mock.MockedTelegramBotsApiTest;
import name.maratik.cw.eu.cwshopbot.model.Shop;
import name.maratik.cw.eu.cwshopbot.model.ShopLine;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ShopDaoTest extends MockedTelegramBotsApiTest {
    private static final String SHOP_CODE = "shopCode";

    @Autowired
    private ShopDao shopDao;

    @Test
    public void putTest() throws DaoException {
        Shop shop = createShop();
        shopDao.putShop(shop);
        Shop result = shopDao.getShopByCode(SHOP_CODE).orElseThrow(RuntimeException::new).build();
        assertThat(result, samePropertyValuesAs(shop));
    }

    @Test
    public void putTwiceTest() throws DaoException {
        Shop shop = createShop();
        shopDao.putShop(shop);
        shopDao.putShop(shop);
        Shop result = shopDao.getShopByCode(SHOP_CODE).orElseThrow(RuntimeException::new).build();
        assertThat(result, samePropertyValuesAs(shop));
    }

    @Test
    public void updateMaxOffersCountTest() throws DaoException {
        Shop shop = createShop();
        shopDao.putShop(shop);
        shop = createShopBuilder(shop.getMaxOffersCount() + 5).build();
        shopDao.putShop(shop);
        Shop result = shopDao.getShopByCode(SHOP_CODE).orElseThrow(RuntimeException::new).build();
        assertThat(result, samePropertyValuesAs(shop));
    }

    @SuppressWarnings("WeakerAccess")
    public static Shop createShop(ShopLine... shopLines) {
        return createShopBuilder(shopLines).build();
    }

    @SuppressWarnings("WeakerAccess")
    public static Shop.Builder createShopBuilder(ShopLine... shopLines) {
        return createShopBuilder(5, shopLines);
    }

    @SuppressWarnings("WeakerAccess")
    public static Shop.Builder createShopBuilder(int maxOffersCount, ShopLine... shopLines) {
        Shop.Builder builder = Shop.builder()
            .setShopCode(SHOP_CODE)
            .setShopName("shopName")
            .setShopCommand("/ws_shopCode")
            .setShopNumber(1)
            .setMaxOffersCount(maxOffersCount)
            .setCharName("charName");
        Arrays.stream(shopLines).forEach(builder::addShopLine);
        return builder;
    }
}
