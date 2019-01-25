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
package name.maratik.cw.cwshopbot.application.repository.deal;

import name.maratik.cw.cwshopbot.application.config.ClockHolder;
import name.maratik.cw.cwshopbot.application.repository.account.AccountRepository;
import name.maratik.cw.cwshopbot.entity.AccountEntity;
import name.maratik.cw.cwshopbot.entity.DealEntity;
import name.maratik.cw.cwshopbot.mock.MockedTest;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import static name.maratik.cw.cwshopbot.application.repository.RepositoryUtils.fetchAssertion;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class DealRepositoryTest extends MockedTest {
    @Autowired
    private DealRepository dealRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ClockHolder clockHolder;

    @Test
    public void shouldSaveAndFind() {
        transactionTemplate.execute(status -> {
            AccountEntity sellerAccount = accountRepository.save(DealEntityUtils.createSellerAccount(clockHolder));
            AccountEntity buyerAccount = accountRepository.save(DealEntityUtils.createBuyerAccount(clockHolder));
            DealEntity beforeDealEntity = DealEntityUtils.createDealEntity(
                sellerAccount.getId(),
                buyerAccount.getId(),
                clockHolder
            );
            DealEntity afterDealEntity = dealRepository.save(beforeDealEntity);
            DealEntity expectedDealEntity = beforeDealEntity.withId(afterDealEntity.getId());
            assertThat(afterDealEntity, samePropertyValuesAs(expectedDealEntity));
            assertThat(
                dealRepository.findById(afterDealEntity.getId())
                    .orElseThrow(fetchAssertion("deal")),
                samePropertyValuesAs(expectedDealEntity)
            );
            return null;
        });
    }
}
