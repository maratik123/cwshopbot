package name.maratik.cw.eu.cwshopbot.model.cwasset;

import name.maratik.cw.eu.cwshopbot.util.Emoji;
import name.maratik.cw.eu.cwshopbot.util.EnumWithCode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Optional;

import static name.maratik.cw.eu.cwshopbot.util.Emoji.DEER;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.DRAGON;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.EAGLE;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.MOON;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.SHARK;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.WOLF;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum Castle implements EnumWithCode {
    MOONLIGHT("Moonlight", MOON),
    WOLFPACK("Wolfpack", WOLF),
    POTATO("Potato", Emoji.POTATO),
    SHARKTEETH("Sharkteeth", SHARK),
    HIGHNEST("Highnest", EAGLE),
    DEERHORN("Deerhorn", DEER),
    DRAGONSCALE("Dragonscale", DRAGON);

    private final String code;
    private final String gameName;
    private static final Map<String, Castle> cache = Util.createCache(values());

    Castle(String code, String emoji) {
        this.code = code;
        this.gameName = emoji + code;
    }

    @JsonValue
    @Override
    public String getCode() {
        return code;
    }

    public String getGameName() {
        return gameName;
    }

    @JsonCreator
    public static Optional<Castle> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }
}
