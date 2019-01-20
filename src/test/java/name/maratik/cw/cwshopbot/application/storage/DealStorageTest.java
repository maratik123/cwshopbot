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
package name.maratik.cw.cwshopbot.application.storage;

import name.maratik.cw.cwshopbot.mock.MockedTest;
import name.maratik.cw.cwshopbot.model.cwapi.Deal;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static name.maratik.cw.cwshopbot.application.repository.RepositoryUtils.fetchAssertion;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class DealStorageTest extends MockedTest {
    @Autowired
    private DealStorage dealStorage;

    @Test
    public void shouldSaveAndGetDeal() {
        Deal deal = StorageUtils.createDeal().build();

        long id = dealStorage.saveDeal(deal);

        Deal storedDeal = dealStorage.findDeal(id).orElseThrow(fetchAssertion("deal"));

        assertThat(storedDeal, samePropertyValuesAs(deal));
    }

}
