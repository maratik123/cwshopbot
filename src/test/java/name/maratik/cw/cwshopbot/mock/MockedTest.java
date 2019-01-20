//    cwshopbot
//    Copyright (C) 2019  Marat Bukharov.
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
package name.maratik.cw.cwshopbot.mock;

import name.maratik.cw.cwshopbot.application.Application;
import name.maratik.cw.cwshopbot.application.config.InternalConfig;
import name.maratik.cw.cwshopbot.application.config.MocksConfig;
import name.maratik.cw.cwshopbot.application.config.PostgresEmbeddedConfig;
import name.maratik.cw.cwshopbot.application.db.DbCleaner;
import name.maratik.cw.cwshopbot.runner.CwShopBotSpringRunner;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.Mockito.reset;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@RunWith(CwShopBotSpringRunner.class)
@TestPropertySource("classpath:test-application.properties")
@ContextConfiguration(classes = {
    InternalConfig.class,
    MocksConfig.class,
    PostgresEmbeddedConfig.class
})
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWebMvc
@AutoConfigureCache
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(Configuration.class),
    basePackageClasses = { Application.class }
)
public abstract class MockedTest {
    @Autowired
    private Map<Object, Consumer<Object>> mocks;

    @Autowired
    private DbCleaner dbCleaner;

    @Autowired
    private OffsetClockHolder offsetClockHolder;

    @Before
    public void init() {
        setupLocale();
        offsetClockHolder.reset();
        dbCleaner.clearDb();
        resetMocks();
    }

    private void resetMocks() {
        mocks.forEach((mock, resetAction) -> {
            reset(mock);
            resetAction.accept(mock);
        });
    }

    private static void setupLocale() {
        Locale.setDefault(new Locale("en"));
    }
}
