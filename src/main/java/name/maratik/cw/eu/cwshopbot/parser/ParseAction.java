package name.maratik.cw.eu.cwshopbot.parser;

import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@FunctionalInterface
public interface ParseAction<T> {
    Optional<T> action();
}
