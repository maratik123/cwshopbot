package name.maratik.cw.eu.cwshopbot.model.cwasset;

import name.maratik.cw.eu.cwshopbot.util.EnumWithCode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum Profession implements EnumWithCode {
    BLACKSMITH("Blacksmith"),
    ALCHEMIST("Alchemist");

    private final String code;
    private final static Map<String, Profession> cache = Util.createCache(values());

    Profession(String code) {
        this.code = code;
    }

    @JsonValue
    @Override
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static Optional<Profession> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }
}
