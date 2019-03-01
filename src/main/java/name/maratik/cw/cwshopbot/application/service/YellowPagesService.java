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
package name.maratik.cw.cwshopbot.application.service;

import name.maratik.cw.cwshopbot.application.storage.YellowPagesStorage;
import name.maratik.cw.cwshopbot.model.NavigableYellowPage;
import name.maratik.cw.cwshopbot.model.cwapi.YellowPage;
import name.maratik.spring.telegram.util.Localizable;

import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
public class YellowPagesService extends Localizable {
    private static final Pattern UNDERSCORE = Pattern.compile("_", Pattern.LITERAL);
    private static final String UNDERSCORE_REPLACEMENT = "\\\\_";

    private final YellowPagesStorage yellowPagesStorage;

    public YellowPagesService(YellowPagesStorage yellowPagesStorage) {
        this.yellowPagesStorage = yellowPagesStorage;
    }

    public Optional<Map.Entry<NavigableYellowPage, String>> formattedYellowPages(String key) {
        var navigableYellowPage = fetchYellowPages(key);

        return navigableYellowPage.getYellowPage().map(yellowPage -> {
            var sb = new StringBuilder(t("YellowPages.INFO",
                yellowPage.getLink(),
                UNDERSCORE.matcher(yellowPage.getName()).replaceAll(UNDERSCORE_REPLACEMENT),
                UNDERSCORE.matcher(yellowPage.getOwnerName()).replaceAll(UNDERSCORE_REPLACEMENT),
                yellowPage.getOwnerCastle().getCode(),
                yellowPage.getKind().getCode(),
                yellowPage.getMana(),
                t(yellowPage.isActive()
                    ? "YellowPages.INFO.ACTIVE"
                    : "YellowPages.INFO.INACTIVE"
                )
            ));

            if (yellowPage.getGuildDiscount() != 0) {
                sb.append(t("YellowPages.INFO.guildDiscount", yellowPage.getGuildDiscount()));
            }
            if (yellowPage.getCastleDiscount() != 0) {
                sb.append(t("YellowPages.INFO.castleDiscount", yellowPage.getCastleDiscount()));
            }

            if (!yellowPage.getOffers().isEmpty()) {
                yellowPage.getOffers().forEach(offer -> formatOfferLine(sb, offer));
                sb.append('\n');
            }

            if (!yellowPage.getSpecialization().isEmpty()) {
                sb.append(t("YellowPages.SPECIALIZATION.HEADER"));
                yellowPage.getSpecialization().forEach((kind, value) ->
                    sb.append(t(kind)).append(": ").append(value).append('\n')
                );
            }

            return new AbstractMap.SimpleImmutableEntry<>(navigableYellowPage, sb.toString());
        });
    }

    private void formatOfferLine(StringBuilder sb, YellowPage.Offer offer) {
//        Optional<Item> items = itemSearchService.findItemByNameList(page.getItem(), true, false)
//            .stream()
//            .limit(1)
//            .findAny();
        sb.append(t("YellowPages.OFFER", offer.getItem(), offer.getMana(), offer.getPrice()));
    }

    private NavigableYellowPage fetchYellowPages(String key) {
        return Optional.ofNullable(key)
            .map(yellowPagesStorage::findYellowPage)
            .orElseGet(() -> yellowPagesStorage.findYellowPage(""));
    }
}
