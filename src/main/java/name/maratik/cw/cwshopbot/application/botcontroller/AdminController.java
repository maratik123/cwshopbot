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
package name.maratik.cw.cwshopbot.application.botcontroller;

import name.maratik.cw.cwshopbot.application.config.ForwardUser;
import name.maratik.cw.cwshopbot.application.service.CWParser;
import name.maratik.cw.cwshopbot.application.service.ChatWarsAuthService;
import name.maratik.cw.cwshopbot.application.service.ItemSearchService;
import name.maratik.cw.cwshopbot.application.service.StatsService;
import name.maratik.cw.cwshopbot.model.ForwardKey;
import name.maratik.cw.cwshopbot.model.parser.ParsedHero;
import name.maratik.cw.cwshopbot.model.parser.ParsedShopEdit;
import name.maratik.cw.cwshopbot.model.parser.ParsedShopInfo;
import name.maratik.spring.telegram.TelegramBotService;
import name.maratik.spring.telegram.annotation.TelegramBot;
import name.maratik.spring.telegram.annotation.TelegramCommand;
import name.maratik.spring.telegram.model.TelegramMessageCommand;

import com.google.common.cache.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Clock;
import java.time.Duration;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@TelegramBot("${name.maratik.cw.cwshopbot.admin}")
public class AdminController extends ShopController {
    private static final Logger logger = LogManager.getLogger(AdminController.class);

    private final StatsService statsService;
    private final DefaultAbsSender client;
    private final long devUserId;

    public AdminController(Clock clock, Duration forwardStale,
                           @ForwardUser Cache<ForwardKey, Long> forwardUserCache,
                           CWParser<ParsedShopInfo> shopInfoParser, CWParser<ParsedShopEdit> shopEditParser,
                           CWParser<ParsedHero> heroParser, ItemSearchService itemSearchService,
                           @Value("${name.maratik.cw.cwshopbot.dev}") long devUserId,
                           @Value("${name.maratik.cw.cwshopbot.dev.username}") String devUserName,
                           StatsService statsService, TelegramBotService telegramBotService,
                           ChatWarsAuthService chatWarsAuthService, @Value("${cwusername}") String cwUserName
    ) {
        super(clock, forwardStale, forwardUserCache, shopInfoParser, shopEditParser, heroParser, itemSearchService,
            devUserId, devUserName, chatWarsAuthService, cwUserName);
        this.devUserId = devUserId;
        this.statsService = statsService;
        this.client = telegramBotService.getClient();
    }

    @PostConstruct
    public void init() throws TelegramApiException {
        client.execute(new SendMessage()
            .setChatId(devUserId)
            .setText(t("AdminController.BOT.UP"))
        );
    }

    @PreDestroy
    public void destroy() {
        try {
            client.execute(new SendMessage()
                .setChatId(devUserId)
                .setText(t("AdminController.BOT.DOWN"))
            );
        } catch (TelegramApiException e) {
            logger.error("Can not send goodbye", e);
        }
    }

    @TelegramCommand(commands = "/stats", description = "#{@loc.t('AdminController.STATS.COMMON')}")
    public SendMessage getStat(long userId) {
        return new SendMessage()
            .setChatId(userId)
            .setText(statsService.getStats());
    }

    @TelegramCommand(commands = "/stats_cache", description = "#{@loc.t('AdminController.STATS.CACHES')}")
    public SendMessage getCacheStats(long userId) {
        return new SendMessage()
            .setChatId(userId)
            .setText(statsService.getCacheStats());
    }

    @TelegramCommand(commands = "/send", description = "#{@loc.t('AdminController.SEND_MESSAGE')}")
    public SendMessage sendMessage(TelegramMessageCommand messageCommand, long userId, DefaultAbsSender client) {
        String[] args = messageCommand.getArgument()
            .map(arg -> arg.split(" ", 2))
            .filter(arr -> arr.length == 2)
            .orElseGet(() -> new String[] {Long.toString(userId), t("AdminController.SEND_MESSAGE.WRONG_COMMAND")});

        try {
            client.execute(new SendMessage()
                .setChatId(args[0])
                .setText(args[1])
            );
            return new SendMessage()
                .setChatId(userId)
                .setText(t("AdminController.SEND_MESSAGE.SUCCESS"));
        } catch (TelegramApiException e) {
            logger.error("Send message wrong", e);
            return new SendMessage()
                .setChatId(userId)
                .setText(t("AdminController.SEND_MESSAGE.ERROR", e.getMessage()));
        }
    }
}
