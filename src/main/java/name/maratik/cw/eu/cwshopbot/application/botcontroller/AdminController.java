//    cwshopbot
//    Copyright (C) 2018  Marat Bukharov.
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
package name.maratik.cw.eu.cwshopbot.application.botcontroller;

import com.google.common.cache.Cache;
import name.maratik.cw.eu.cwshopbot.application.config.ForwardUser;
import name.maratik.cw.eu.cwshopbot.application.service.CWParser;
import name.maratik.cw.eu.cwshopbot.application.service.ItemSearchService;
import name.maratik.cw.eu.cwshopbot.model.ForwardKey;
import name.maratik.cw.eu.cwshopbot.model.parser.ParsedShopInfo;
import name.maratik.cw.eu.spring.annotation.TelegramBot;
import name.maratik.cw.eu.spring.annotation.TelegramCommand;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.time.Clock;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@TelegramBot("${name.maratik.cw.eu.cwshopbot.admin}")
public class AdminController extends ShopController {
    private final Cache<ForwardKey, Long> forwardUserCache;

    public AdminController(Clock clock, @Value("${forwardStaleSec}") int forwardStaleSec,
                           @ForwardUser Cache<ForwardKey, Long> forwardUserCache,
                           CWParser<ParsedShopInfo> shopInfoParser, ItemSearchService itemSearchService,
                           @Value("${name.maratik.cw.eu.cwshopbot.dev}") long adminUserId) {
        super(clock, forwardStaleSec, forwardUserCache, shopInfoParser, itemSearchService, adminUserId);
        this.forwardUserCache = forwardUserCache;
    }

    @TelegramCommand(commands = "/stats", description = "Get statistics")
    public SendMessage getStat(long userId) {
        return new SendMessage()
            .setChatId(userId)
            .setText("User cache = " + forwardUserCache.stats());
    }
}
