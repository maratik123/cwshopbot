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

import name.maratik.cw.cwshopbot.model.cwapi.YellowPage;
import name.maratik.spring.telegram.util.Localizable;

import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Pattern;

import static java.util.Collections.newSetFromMap;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
public class YellowPagesService extends Localizable {
    private static final Pattern UNDERSCORE = Pattern.compile("_", Pattern.LITERAL);
    private static final String UNDERSCORE_REPLACEMENT = "\\\\_";

    private final NavigableMap<String, YellowPage> yellowPagesStorage = new ConcurrentSkipListMap<>();
    private final Set<String> activeStores = newSetFromMap(new ConcurrentHashMap<>());

    public void storeYellowPages(List<YellowPage> yellowPages) {
        activeStores.clear();
        yellowPages.forEach(yellowPage -> {
            yellowPagesStorage.put(yellowPage.getLink(), yellowPage);
            activeStores.add(yellowPage.getLink());
        });
    }

    public Optional<Map.Entry<String, String>> formattedYellowPages(String key) {
        return fetchYellowPages(key).map(yellowPage -> {
            StringBuilder sb = new StringBuilder(t("YellowPages.INFO",
                yellowPage.getLink(),
                UNDERSCORE.matcher(yellowPage.getName()).replaceAll(UNDERSCORE_REPLACEMENT),
                UNDERSCORE.matcher(yellowPage.getOwnerName()).replaceAll(UNDERSCORE_REPLACEMENT),
                yellowPage.getOwnerCastle().getCode(),
                yellowPage.getKind().getCode(),
                yellowPage.getMana(),
                t(activeStores.contains(yellowPage.getLink())
                    ? "YellowPages.INFO.ACTIVE"
                    : "YellowPages.INFO.INACTIVE"
                )
            ));

            if (!yellowPage.getOffers().isEmpty()) {
                yellowPage.getOffers().forEach(offer -> formatOfferLine(sb, offer));
                sb.append('\n');
            }

            if (!yellowPage.getSpecialization().isEmpty()) {
                sb.append(t("YellowPages.SPECIALIZATION.HEADER"));
                yellowPage.getSpecialization().forEach((kind, value) ->
                    sb.append(t("YellowPages.SPECIALIZATION", kind, value))
                );
            }

            return new AbstractMap.SimpleImmutableEntry<>(yellowPage.getLink(), sb.toString());
        });
    }

    private void formatOfferLine(StringBuilder sb, YellowPage.Offer offer) {
//        Optional<Item> items = itemSearchService.findItemByNameList(page.getItem(), true, false)
//            .stream()
//            .limit(1)
//            .findAny();
        sb.append(t("YellowPages.OFFER", offer.getItem(), offer.getMana(), offer.getPrice()));
    }

    private Optional<YellowPage> fetchYellowPages() {
        return Optional.ofNullable(yellowPagesStorage.firstEntry())
            .map(Map.Entry::getValue);
    }

    private Optional<YellowPage> fetchYellowPages(String key) {
        return Optional.ofNullable(key)
            .map(yellowPagesStorage::ceilingEntry)
            .map(Map.Entry::getValue)
            .map(Optional::of)
            .orElseGet(this::fetchYellowPages);
    }

    public Optional<String> previousKey(String key) {
        return Optional.ofNullable(key)
            .map(yellowPagesStorage::lowerKey);
    }

    public Optional<String> nextKey(String key) {
        return Optional.ofNullable(key)
            .map(yellowPagesStorage::higherKey);
    }
}
