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
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
public class YellowPagesService extends Localizable {
    private static final Pattern UNDERSCORE = Pattern.compile("_", Pattern.LITERAL);
    public static final String UNDERSCORE_REPLACEMENT = "\\\\_";
    private final NavigableMap<String, YellowPages> yellowPagesStorage = new ConcurrentSkipListMap<>();
    private final Set<String> activeStores = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void storeYellowPages(List<YellowPages> yellowPagesList) {
        activeStores.clear();
        yellowPagesList.forEach(yellowPages -> {
            yellowPagesStorage.put(yellowPages.getLink(), yellowPages);
            activeStores.add(yellowPages.getLink());
        });
    }

    public Optional<Map.Entry<String, String>> formattedYellowPages(String key) {
        return fetchYellowPages(key).map(yellowPages -> {
            StringBuilder sb = new StringBuilder(t("YellowPages.INFO",
                yellowPages.getLink(),
                UNDERSCORE.matcher(yellowPages.getName()).replaceAll(UNDERSCORE_REPLACEMENT),
                UNDERSCORE.matcher(yellowPages.getOwnerName()).replaceAll(UNDERSCORE_REPLACEMENT),
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

            return new AbstractMap.SimpleImmutableEntry<>(yellowPages.getLink(), sb.toString());
        });
    }

    private Optional<YellowPages> fetchYellowPages() {
        return Optional.ofNullable(yellowPagesStorage.firstEntry())
            .map(Map.Entry::getValue);
    }

    private Optional<YellowPages> fetchYellowPages(String key) {
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
