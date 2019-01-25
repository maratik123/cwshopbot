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

import name.maratik.cw.cwshopbot.application.config.ClockHolder;
import name.maratik.cw.cwshopbot.application.repository.account.AccountRepository;
import name.maratik.cw.cwshopbot.application.repository.deal.DealRepository;
import name.maratik.cw.cwshopbot.entity.AccountEntity;
import name.maratik.cw.cwshopbot.entity.DealEntity;
import name.maratik.cw.cwshopbot.model.cwapi.CastleByEmoji;
import name.maratik.cw.cwshopbot.model.cwapi.Deal;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Repository
public class DealStorage {
    private final DealRepository dealRepository;
    private final AccountRepository accountRepository;
    private final ClockHolder clockHolder;

    public DealStorage(DealRepository dealRepository, AccountRepository accountRepository, ClockHolder clockHolder,
                       @SuppressWarnings("unused") SpringLiquibase springLiquibase) {
        this.dealRepository = dealRepository;
        this.accountRepository = accountRepository;
        this.clockHolder = clockHolder;
    }

    @PostConstruct
    public void ensureBeansInitialized() {
    }

    @Transactional
    public long saveDeal(Deal deal) {
        LocalDateTime now = LocalDateTime.now(clockHolder);
        AccountEntity sellerAccount = accountRepository.save(AccountEntity.ofSeller(deal, now));
        AccountEntity buyerAccount = accountRepository.save(AccountEntity.ofBuyer(deal, now));
        return dealRepository.save(DealEntity.of(deal, sellerAccount.getId(), buyerAccount.getId(), now)).getId();
    }

    public Optional<Deal> findDeal(long id) {
        return dealRepository.findById(id)
            .flatMap(dealEntity -> accountRepository.findById(dealEntity.getSellerAccountId())
                .flatMap(sellerAccount -> accountRepository.findById(dealEntity.getBuyerAccountId())
                    .map(buyerAccount ->
                        Deal.builder()
                            .sellerId(sellerAccount.getExternalId())
                            .sellerName(sellerAccount.getName())
                            .sellerCastle(CastleByEmoji.findByCastle(sellerAccount.getCastle()))
                            .buyerId(buyerAccount.getExternalId())
                            .buyerName(buyerAccount.getName())
                            .buyerCastle(CastleByEmoji.findByCastle(buyerAccount.getCastle()))
                            .item(dealEntity.getItem())
                            .qty(dealEntity.getQty())
                            .price(dealEntity.getPrice())
                            .build()
                    )
                )
            );
    }
}
