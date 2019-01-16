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
package name.maratik.cw.cwshopbot.application.repository.yellow.page;

import name.maratik.cw.cwshopbot.entity.YellowPageEntity;
import name.maratik.cw.cwshopbot.mock.MockedTest;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import static name.maratik.cw.cwshopbot.application.repository.yellow.page.YellowPageEntityUtils.createYellowPageEntity;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class YellowPageRepositoryTest extends MockedTest {
    @Autowired
    private YellowPageRepository yellowPageRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void shouldSaveAndGetYellowPage() {
        transactionTemplate.execute(status -> {
            YellowPageEntity yellowPageEntity = createYellowPageEntity().build();
            yellowPageRepository.save(yellowPageEntity);

            YellowPageEntity fetchedYellowPageEntity = yellowPageRepository.findByLink(yellowPageEntity.getLink())
                .orElseThrow(() -> new AssertionError("Could not fetch yellow_page"));

            assertThat(fetchedYellowPageEntity, samePropertyValuesAs(yellowPageEntity));

            return null;
        });
    }

    @Test
    public void shouldUpdateYellowPage() {
        transactionTemplate.execute(status -> {
            YellowPageEntity yellowPageEntity = createYellowPageEntity()
                .active(true)
                .build();
            yellowPageRepository.save(yellowPageEntity);

            yellowPageEntity = yellowPageRepository.findByLink(yellowPageEntity.getLink())
                .orElseThrow(() -> new AssertionError("Could not fetch yellow_page"));

            assertTrue(yellowPageEntity.isActive());

            yellowPageEntity = yellowPageEntity.toBuilder()
                .active(false)
                .build();

            yellowPageRepository.save(yellowPageEntity);

            yellowPageEntity = yellowPageRepository.findByLink(yellowPageEntity.getLink())
                .orElseThrow(() -> new AssertionError("Could not fetch yellow_page"));

            assertFalse(yellowPageEntity.isActive());

            return null;
        });
    }

    @Test
    public void shouldBatchedSaveAndGetYellowPage() {
        transactionTemplate.execute(status -> {
            YellowPageEntity yellowPageEntity = createYellowPageEntity().build();
            YellowPageEntity anotherYellowPageEntity = yellowPageEntity.toBuilder()
                .link(yellowPageEntity.getLink() + '1')
                .build();
            yellowPageRepository.saveAll(ImmutableList.of(yellowPageEntity, anotherYellowPageEntity));

            YellowPageEntity fetchedYellowPageEntity = yellowPageRepository.findByLink(yellowPageEntity.getLink())
                .orElseThrow(() -> new AssertionError("Could not fetch yellow_page"));

            assertThat(fetchedYellowPageEntity, samePropertyValuesAs(yellowPageEntity));

            fetchedYellowPageEntity = yellowPageRepository.findByLink(anotherYellowPageEntity.getLink())
                .orElseThrow(() -> new AssertionError("Could not fetch yellow_page"));

            assertThat(fetchedYellowPageEntity, samePropertyValuesAs(anotherYellowPageEntity));

            return null;
        });
    }

    @Test
    public void shouldInactiveYellowPage() {
        transactionTemplate.execute(status -> {
            YellowPageEntity yellowPageEntity = createYellowPageEntity()
                .active(true)
                .build();
            yellowPageRepository.save(yellowPageEntity);

            yellowPageEntity = yellowPageRepository.findByLink(yellowPageEntity.getLink())
                .orElseThrow(() -> new AssertionError("Could not fetch yellow_page"));

            assertTrue(yellowPageEntity.isActive());

            yellowPageRepository.setAllInactive();

            yellowPageEntity = yellowPageRepository.findByLink(yellowPageEntity.getLink())
                .orElseThrow(() -> new AssertionError("Could not fetch yellow_page"));

            assertFalse(yellowPageEntity.isActive());

            return null;
        });
    }
}
