package name.maratik.cw.eu.cwshopbot.service;

import name.maratik.cw.eu.cwshopbot.model.ShopInfo;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public interface CWParser<T> {
    Optional<ShopInfo> parse(Message message);
}
