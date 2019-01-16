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

import name.maratik.cw.cwshopbot.application.repository.yellow.page.YellowPageRepository;
import name.maratik.cw.cwshopbot.application.repository.yellow.page.offer.YellowPageOfferRepository;
import name.maratik.cw.cwshopbot.application.repository.yellow.page.specialization.YellowPageSpecializationRepository;
import name.maratik.cw.cwshopbot.entity.YellowPageEntity;
import name.maratik.cw.cwshopbot.entity.YellowPageOfferEntity;
import name.maratik.cw.cwshopbot.entity.YellowPageSpecializationEntity;
import name.maratik.cw.cwshopbot.model.NavigableYellowPage;
import name.maratik.cw.cwshopbot.model.cwapi.CastleByEmoji;
import name.maratik.cw.cwshopbot.model.cwapi.ProfessionByEmoji;
import name.maratik.cw.cwshopbot.model.cwapi.YellowPage;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Repository
public class YellowPagesStorage {
    private final YellowPageRepository yellowPageRepository;
    private final YellowPageOfferRepository yellowPageOfferRepository;
    private final YellowPageSpecializationRepository yellowPageSpecializationRepository;

    public YellowPagesStorage(YellowPageRepository yellowPageRepository,
                              YellowPageOfferRepository yellowPageOfferRepository,
                              YellowPageSpecializationRepository yellowPageSpecializationRepository) {
        this.yellowPageRepository = yellowPageRepository;
        this.yellowPageOfferRepository = yellowPageOfferRepository;
        this.yellowPageSpecializationRepository = yellowPageSpecializationRepository;
    }

    @Transactional
    public void saveYellowPages(List<YellowPage> yellowPages) {
        yellowPageRepository.setAllInactive();
        Set<String> links = yellowPages.stream()
            .map(YellowPage::getLink)
            .collect(toImmutableSet());
        if (!links.isEmpty()) {
            yellowPageOfferRepository.setInactiveForYellowPages(links);
            yellowPageSpecializationRepository.zeroValueForYellowPages(links);
        }

        yellowPageRepository.saveAll(yellowPages.stream()
            .map(yellowPage -> YellowPageEntity.builder()
                .link(yellowPage.getLink())
                .name(yellowPage.getName())
                .ownerName(yellowPage.getOwnerName())
                .ownerCastle(yellowPage.getOwnerCastle().getCastle())
                .profession(yellowPage.getKind().getProfession())
                .mana(yellowPage.getMana())
                .active(true)
                .build()
            )
            .collect(toImmutableList())
        );
        yellowPageOfferRepository.saveAll(yellowPages.stream()
            .flatMap(yellowPage -> yellowPage.getOffers().stream()
                .map(offer -> YellowPageOfferEntity.builder()
                    .yellowPage(yellowPage.getLink())
                    .item(offer.getItem())
                    .price(offer.getPrice())
                    .mana(offer.getMana())
                    .active(true)
                    .build()
                )
            )
            .collect(toImmutableList())
        );
        yellowPageSpecializationRepository.saveAll(yellowPages.stream()
            .flatMap(yellowPage -> yellowPage.getSpecialization().entrySet().stream()
                .map(entry -> YellowPageSpecializationEntity.builder()
                    .yellowPage(yellowPage.getLink())
                    .specialization(entry.getKey())
                    .value(entry.getValue())
                    .build()
                )
            )
            .collect(toImmutableList())
        );
    }

    public NavigableYellowPage findYellowPage(String link) {
        Optional<YellowPage> yellowPage = yellowPageRepository.findFirstByLinkGreaterThanOrderByLink(link)
            .map(yellowPageEntity -> YellowPage.builder()
                .link(yellowPageEntity.getLink())
                .name(yellowPageEntity.getName())
                .ownerName(yellowPageEntity.getOwnerName())
                .ownerCastle(CastleByEmoji.findByCastle(yellowPageEntity.getOwnerCastle()))
                .kind(ProfessionByEmoji.findByProfession(yellowPageEntity.getProfession()))
                .mana(yellowPageEntity.getMana())
                .active(yellowPageEntity.isActive())
                .offers(yellowPageOfferRepository.findByYellowPage(yellowPageEntity.getLink())
                    .map(yellowPageOfferEntity -> YellowPage.Offer.builder()
                        .item(yellowPageOfferEntity.getItem())
                        .mana(yellowPageOfferEntity.getMana())
                        .price(yellowPageOfferEntity.getPrice())
                        .active(yellowPageOfferEntity.isActive())
                        .build()
                    )
                    .collect(toImmutableList())
                )
                .specialization(yellowPageSpecializationRepository.findByYellowPage(yellowPageEntity.getLink())
                    .collect(toImmutableMap(
                        YellowPageSpecializationEntity.Content::getSpecialization,
                        YellowPageSpecializationEntity.Content::getValue
                    ))
                )
                .build()
            );
        return NavigableYellowPage.builder()
            .yellowPage(yellowPage)
            .previousLink(yellowPage
                .map(YellowPage::getLink)
                .flatMap(yellowPageRepository::findTopByLinkBeforeOrderByLink)
                .map(YellowPageEntity.Link::getLink)
            )
            .nextLink(yellowPage
                .map(YellowPage::getLink)
                .flatMap(yellowPageRepository::findFirstByLinkAfterOrderByLink)
                .map(YellowPageEntity.Link::getLink)
            )
            .build();
    }
}
