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
package name.maratik.cw.cwshopbot.application.repository.yellow.page.specialization;

import name.maratik.cw.cwshopbot.application.config.ClockHolder;
import name.maratik.cw.cwshopbot.application.repository.yellow.page.YellowPageRepository;
import name.maratik.cw.cwshopbot.entity.YellowPageEntity;
import name.maratik.cw.cwshopbot.entity.YellowPageSpecializationEntity;
import name.maratik.cw.cwshopbot.mock.MockedTest;
import name.maratik.cw.cwshopbot.model.cwapi.Specialization;

import com.google.common.collect.ImmutableList;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static name.maratik.cw.cwshopbot.application.repository.RepositoryUtils.fetchAssertion;
import static name.maratik.cw.cwshopbot.application.repository.yellow.page.YellowPageEntityUtils.createYellowPageEntity;
import static name.maratik.cw.cwshopbot.application.repository.yellow.page.YellowPageEntityUtils.createYellowPageSpecializationEntity;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class YellowPageSpecializationRepositoryTest extends MockedTest {
    @Autowired
    private YellowPageSpecializationRepository yellowPageSpecializationRepository;

    @Autowired
    private YellowPageRepository yellowPageRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ClockHolder clockHolder;

    @Test
    public void shouldSaveAndGetYellowPageSpecialization() {
        transactionTemplate.execute(status -> {
            YellowPageEntity yellowPageEntity = createYellowPageEntity(clockHolder).build();
            yellowPageRepository.save(yellowPageEntity);

            YellowPageSpecializationEntity yellowPageSpecializationEntity = createYellowPageSpecializationEntity().build();
            yellowPageSpecializationRepository.save(yellowPageSpecializationEntity);

            List<YellowPageSpecializationEntity.Content> fetchedYellowPageSpecializationContents = yellowPageSpecializationRepository
                .findByYellowPageAndValueGreaterThan0(yellowPageEntity.getLink())
                .collect(toImmutableList());

            assertThat(fetchedYellowPageSpecializationContents, contains(specializationMatches(yellowPageSpecializationEntity)));

            return null;
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldBatchedSaveAndGetYellowPageSpecialization() {
        transactionTemplate.execute(status -> {
            YellowPageEntity yellowPageEntity = createYellowPageEntity(clockHolder).build();
            yellowPageRepository.save(yellowPageEntity);

            YellowPageSpecializationEntity yellowPageSpecializationEntity = createYellowPageSpecializationEntity()
                .specialization(Specialization.BOOTS)
                .build();
            YellowPageSpecializationEntity anotherYellowPageSpecializationEntity = yellowPageSpecializationEntity.toBuilder()
                .specialization(Specialization.ARMOR)
                .build();
            yellowPageSpecializationRepository.saveAll(ImmutableList.of(yellowPageSpecializationEntity, anotherYellowPageSpecializationEntity));

            List<YellowPageSpecializationEntity.Content> fetchedYellowPageSpecializationContents = yellowPageSpecializationRepository
                .findByYellowPageAndValueGreaterThan0(yellowPageEntity.getLink())
                .collect(toImmutableList());

            assertThat(fetchedYellowPageSpecializationContents, containsInAnyOrder(
                specializationMatches(yellowPageSpecializationEntity),
                specializationMatches(anotherYellowPageSpecializationEntity)
            ));

            return null;
        });
    }

    @Test
    public void shouldUpdateYellowPageSpecialization() {
        transactionTemplate.execute(status -> {
            YellowPageEntity yellowPageEntity = createYellowPageEntity(clockHolder).build();
            yellowPageRepository.save(yellowPageEntity);

            YellowPageSpecializationEntity yellowPageSpecializationEntity = createYellowPageSpecializationEntity()
                .value(50)
                .build();
            yellowPageSpecializationRepository.save(yellowPageSpecializationEntity);
            YellowPageSpecializationEntity.Content yellowPageSpecializationContent = yellowPageSpecializationRepository
                .findByYellowPageAndValueGreaterThan0(yellowPageEntity.getLink())
                .findAny()
                .orElseThrow(fetchAssertion("yellow_page_specialization"));
            assertEquals(50, yellowPageSpecializationContent.getValue());

            yellowPageSpecializationEntity = yellowPageSpecializationEntity.toBuilder()
                .value(100)
                .build();
            yellowPageSpecializationRepository.save(yellowPageSpecializationEntity);
            yellowPageSpecializationContent = yellowPageSpecializationRepository
                .findByYellowPageAndValueGreaterThan0(yellowPageEntity.getLink())
                .findAny()
                .orElseThrow(fetchAssertion("yellow_page_specialization"));
            assertEquals(100, yellowPageSpecializationContent.getValue());

            return null;
        });
    }

    @Test
    public void shouldZeroYellowPageSpecialization() {
        transactionTemplate.execute(status -> {
            YellowPageEntity yellowPageEntity = createYellowPageEntity(clockHolder).build();
            yellowPageRepository.save(yellowPageEntity);

            YellowPageSpecializationEntity yellowPageSpecializationEntity = createYellowPageSpecializationEntity()
                .value(100)
                .build();
            yellowPageSpecializationRepository.save(yellowPageSpecializationEntity);
            YellowPageSpecializationEntity.Content yellowPageSpecializationContent = yellowPageSpecializationRepository
                .findByYellowPageAndValueGreaterThan0(yellowPageEntity.getLink())
                .findAny()
                .orElseThrow(fetchAssertion("yellow_page_specialization"));
            assertEquals(100, yellowPageSpecializationContent.getValue());

            yellowPageSpecializationRepository.zeroValueForYellowPages(singleton(yellowPageEntity.getLink()));
            assertFalse(yellowPageSpecializationRepository
                .findByYellowPageAndValueGreaterThan0(yellowPageEntity.getLink())
                .findAny()
                .isPresent()
            );

            return null;
        });
    }

    private static Matcher<YellowPageSpecializationEntity.Content> specializationMatches(YellowPageSpecializationEntity yellowPageSpecializationEntity) {
        return allOf(
            hasProperty("specialization", equalTo(yellowPageSpecializationEntity.getSpecialization())),
            hasProperty("value", equalTo(yellowPageSpecializationEntity.getValue()))
        );
    }
}
