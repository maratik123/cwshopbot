package name.maratik.cw.cwshopbot.application.service;

import name.maratik.cw.cwshopbot.model.cwapi.YellowPages;
import name.maratik.spring.telegram.util.Localizable;

import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
public class YellowPagesService extends Localizable {
    private NavigableMap<String, YellowPages> yellowPagesStorage = new ConcurrentSkipListMap<>();
    private Set<String> activeStores = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void storeYellowPages(List<YellowPages> yellowPagesList) {
        activeStores.clear();
        yellowPagesList.forEach(yellowPages -> {
            yellowPagesStorage.put(yellowPages.getLink(), yellowPages);
            activeStores.add(yellowPages.getLink());
        });
    }

    public Optional<Map.Entry<String, String>> formattedYellowPages(String key) {
        return fetchYellowPages(key).map(yellowPages -> {
            StringBuilder sb = new StringBuilder();
            sb.append(t("YellowPages.INFO",
                yellowPages.getLink(),
                yellowPages.getName(),
                yellowPages.getOwnerName(),
                yellowPages.getOwnerCastle().getCode(),
                yellowPages.getKind().getCode(),
                yellowPages.getMana(),
                t(activeStores.contains(yellowPages.getLink())
                    ? "YellowPages.INFO.ACTIVE"
                    : "YellowPages.INFO.INACTIVE"
                )
            ));

            if (!yellowPages.getOffers().isEmpty()) {
                yellowPages.getOffers().forEach(offer ->
                    sb.append(t("YellowPages.OFFER", offer.getItem(), offer.getMana(), offer.getPrice()))
                );
                sb.append('\n');
            }

            if (!yellowPages.getSpecialization().isEmpty()) {
                sb.append(t("YellowPages.SPECIALIZATION.HEADER"));
                yellowPages.getSpecialization().forEach((kind, value) ->
                    sb.append(t("YellowPages.SPECIALIZATION", kind, value))
                );
            }

            return new AbstractMap.SimpleImmutableEntry<>(key, sb.toString());
        });
    }

    private Optional<YellowPages> fetchYellowPages() {
        return Optional.ofNullable(yellowPagesStorage.firstEntry())
            .map(Map.Entry::getValue);
    }

    private Optional<YellowPages> fetchYellowPages(String key) {
        return Optional.ofNullable(key)
            .map(yellowPagesStorage::get)
            .map(Optional::of)
            .orElseGet(this::fetchYellowPages);
    }

    public Optional<String> previousKey(String key) {
        return Optional.ofNullable(key)
            .map(yellowPagesStorage::lowerKey);
    }

    public Optional<String> nextKey(String key) {
        return Optional.ofNullable(key)
            .map(yellowPagesStorage::higherKey)
            .map(Optional::of)
            .orElseGet(() -> Optional.ofNullable(yellowPagesStorage.firstEntry())
                .map(Map.Entry::getKey)
            );
    }
}
