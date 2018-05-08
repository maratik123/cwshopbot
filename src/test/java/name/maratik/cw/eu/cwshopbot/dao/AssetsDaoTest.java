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
package name.maratik.cw.eu.cwshopbot.dao;

import name.maratik.cw.eu.cwshopbot.mock.MockedTelegramBotsApiTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@SpringBootTest
public class AssetsDaoTest extends MockedTelegramBotsApiTest {
    private static final Logger logger = LogManager.getLogger(AssetsDaoTest.class);

    @Autowired
    AssetsDao.AssetsDto assetsDto;

    @Test
    public void shouldAssetsDtoLoad() {
        logger.info("Assets: {}", assetsDto);
        assertNotNull(assetsDto);
    }

    @Test
    public void shouldNotEmptyCraftbookMap() {
        assertThat(assetsDto.getCraftbook().keySet(), containsInAnyOrder("1", "2", "3"));
        assetsDto.getCraftbook().values().forEach(craftbook -> assertThat(craftbook.getItems(), not(empty())));
    }
}
