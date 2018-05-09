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
package name.maratik.cw.eu.cwshopbot.botcontroller;

import com.google.common.cache.Cache;
import name.maratik.cw.eu.cwshopbot.config.ForwardUser;
import name.maratik.cw.eu.cwshopbot.model.ForwardKey;
import name.maratik.cw.eu.cwshopbot.model.ShopInfo;
import name.maratik.cw.eu.cwshopbot.service.CWParser;
import name.maratik.cw.eu.cwshopbot.service.ItemSearchService;
import name.maratik.cw.eu.spring.annotation.TelegramBot;
import name.maratik.cw.eu.spring.annotation.TelegramCommand;
import name.maratik.cw.eu.spring.annotation.TelegramForward;
import name.maratik.cw.eu.spring.annotation.TelegramMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@TelegramBot
public class ShopController {
    private static final Logger logger = LogManager.getLogger(ShopController.class);

    private final Clock clock;
    private final int forwardStaleSec;
    private final ConcurrentMap<ForwardKey, Long> forwardUserCache;
    private final CWParser<ShopInfo> shopInfoParser;
    private final ItemSearchService itemSearchService;

    public ShopController(Clock clock, @Value("${forwardStaleSec}") int forwardStaleSec,
                          @ForwardUser Cache<ForwardKey, Long> forwardUserCache,
                          CWParser<ShopInfo> shopInfoParser, ItemSearchService itemSearchService) {
        this.clock = clock;
        this.forwardStaleSec = forwardStaleSec;
        this.forwardUserCache = forwardUserCache.asMap();
        this.shopInfoParser = shopInfoParser;
        this.itemSearchService = itemSearchService;
    }

    @TelegramMessage
    public SendMessage message(long userId, String message) {
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(itemSearchService.findByCode(message));
    }

    @TelegramCommand(commands = "/t_*", description = "Info about item")
    public SendMessage itemInfo(long userId, Update update) {
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(itemSearchService.findByCode(update.getMessage().getText().substring(3)));
    }

    @TelegramCommand(commands = "/view_*", description = "View recipe")
    public SendMessage recipeView(long userId, Update update) {
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(itemSearchService.findRecipeByCode(update.getMessage().getText().substring(6)));
    }

    private static SendMessage processMessage(long userId, String message, User user) {
        logger.info("Incoming message from: {}, data: {}", userId, message);

        return new SendMessage()
            .setChatId(userId)
            .setText(String.format("Hi %s! You've sent me command with argument: \"%s\"",
                user.getFirstName(),
                message
            ));
    }

    @TelegramForward("${cwuserid}")
    public SendMessage forward(Update update, String messageText, User user, long userId, Instant forwardTime,
                               Message message) {
        logger.info("Accepted incoming forward data: {}", messageText);

        if (messageIsStale(forwardTime)) {
            logger.info("Forwarded stale update: {}", update);
            return new SendMessage()
                .setChatId(userId)
                .setText("Please, send fresh forward");
        }

        Long previousUserForwarded = forwardUserCache.putIfAbsent(new ForwardKey(message), userId);

        if (previousUserForwarded != null) {
            return new SendMessage()
                .setChatId(userId)
                .setText(
                    previousUserForwarded != userId
                        ? "This forward is not belong to you"
                        : "I've seen this forward already"
                );
        }

        Optional<ShopInfo> shopInfo = shopInfoParser.parse(message);

        return new SendMessage()
            .setChatId(userId)
            .setText(shopInfo
                .map(s -> "You've sent shop with name='" + s.getShopName() + "'\n" +
                    "for char='" + s.getCharName() + "'\n" +
                    "with command='" + s.getShopCommand() + '\'')
                .orElse("Unknown forward")
            );
    }

    @TelegramForward
    public SendMessage defaultForward(long userId, User user) {
        logger.info("Accepted unsupported forward data from user: ", userId);
        return new SendMessage()
            .setChatId(userId)
            .setText(String.format("Hi %s, I can't recognize you!",
                user.getFirstName()
            ));
    }
    @TelegramCommand(commands = "/license", description = "Terms and conditions")
    public SendMessage license(long userId) {
        return new SendMessage()
            .enableMarkdown(true)
            .setChatId(userId)
            .setText("cwshopbot  Copyright (C) 2018  maratik\n" +
                "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                "This is free software, and you are welcome to redistribute it\n" +
                "under conditions of [GNU Affero Public License v3.0 or later](https://www.gnu.org/licenses/).\n" +
                "For sources see /source");
    }

    @TelegramCommand(commands = "/source", description = "Source code", hidden = true)
    public SendMessage source(long userId) {
        return new SendMessage()
            .enableMarkdown(true)
            .setChatId(userId)
            .setText("[here](https://github.com/maratik123/cwshopbot)");
    }

    private boolean messageIsStale(Instant forwardTime) {
        return clock.instant().minusSeconds(forwardStaleSec).isAfter(forwardTime);
    }
}
