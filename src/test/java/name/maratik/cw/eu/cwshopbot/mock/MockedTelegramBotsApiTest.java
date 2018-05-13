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

import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.TelegramBotsApi;

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
    "cwuserid=987654321"
})
public abstract class MockedTelegramBotsApiTest {
    @MockBean
    protected TelegramBotsApi telegramBotsApi;
}
