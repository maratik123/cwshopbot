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
package name.maratik.cw.cwshopbot.application.dao;

import name.maratik.cw.cwshopbot.mock.MockedTest;
import name.maratik.cw.cwshopbot.model.cwasset.Assets;

import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Import(AssetsDaoTest.TestConfig.class)
public class AssetsDaoTest extends MockedTest {
    private static final Logger logger = LogManager.getLogger(AssetsDaoTest.class);

    @Autowired
    private AssetsDao assetsDao;

    private AssetsDao.AssetsDto assetsDto;

    @Before
    @Override
    public void init() {
        super.init();
        assetsDto = assetsDao.getAssetsDto();
    }

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

    @Test
    public void shouldCreateAssets() {
        Assets assets = assetsDao.createAssets();
        logger.info("Assets: {}", assets);
        assertNotNull(assets);
    }

    @Configuration
    public static class TestConfig {
        @Bean
        public AssetsDao assetsDao(ResourceLoader resourceLoader, TypeFactory typeFactory) throws IOException {
            return new AssetsDao(resourceLoader.getResource("classpath:test-assets.yaml"), typeFactory);
        }
    }
}
