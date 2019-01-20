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
import name.maratik.cw.cwshopbot.application.repository.deal.DealRepository;
import name.maratik.cw.cwshopbot.entity.DealEntity;
import name.maratik.cw.cwshopbot.model.cwapi.CastleByEmoji;
import name.maratik.cw.cwshopbot.model.cwapi.Deal;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Repository
public class DealStorage {
    private final DealRepository dealRepository;
    private final ClockHolder clockHolder;

    public DealStorage(DealRepository dealRepository, ClockHolder clockHolder) {
        this.dealRepository = dealRepository;
        this.clockHolder = clockHolder;
    }

    @Transactional
    public long saveDeal(Deal deal) {
        return dealRepository.save(DealEntity.of(deal, Timestamp.from(clockHolder.instant()))).getId();
    }

    public Optional<Deal> findDeal(long id) {
        return dealRepository.findById(id)
            .map(dealEntity -> Deal.builder()
                .sellerId(dealEntity.getSellerId())
                .sellerName(dealEntity.getSellerName())
                .sellerCastle(CastleByEmoji.findByCastle(dealEntity.getSellerCastle()))
                .buyerId(dealEntity.getBuyerId())
                .buyerName(dealEntity.getBuyerName())
                .buyerCastle(CastleByEmoji.findByCastle(dealEntity.getBuyerCastle()))
                .item(dealEntity.getItem())
                .qty(dealEntity.getQty())
                .price(dealEntity.getPrice())
                .build()
            );
    }
}
