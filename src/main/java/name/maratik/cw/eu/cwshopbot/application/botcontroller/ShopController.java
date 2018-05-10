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
import name.maratik.cw.eu.cwshopbot.model.ForwardKey;
import name.maratik.cw.eu.cwshopbot.model.parser.MessageType;
import name.maratik.cw.eu.cwshopbot.model.parser.ParsedShopInfo;
import name.maratik.cw.eu.cwshopbot.application.service.CWParser;
import name.maratik.cw.eu.cwshopbot.application.service.ItemSearchService;
import name.maratik.cw.eu.spring.annotation.TelegramBot;
import name.maratik.cw.eu.spring.annotation.TelegramCommand;
import name.maratik.cw.eu.spring.annotation.TelegramForward;
import name.maratik.cw.eu.spring.annotation.TelegramHelp;
import name.maratik.cw.eu.spring.annotation.TelegramMessage;
import name.maratik.cw.eu.spring.model.TelegramMessageCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.ForwardMessage;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
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
    private final CWParser<ParsedShopInfo> shopInfoParser;
    private final ItemSearchService itemSearchService;
    private final long adminUserId;

    public ShopController(Clock clock, @Value("${forwardStaleSec}") int forwardStaleSec,
                          @ForwardUser Cache<ForwardKey, Long> forwardUserCache,
                          CWParser<ParsedShopInfo> shopInfoParser, ItemSearchService itemSearchService,
                          @Value("${name.maratik.cw.eu.cwshopbot.admin}") long adminUserId) {
        this.clock = clock;
        this.forwardStaleSec = forwardStaleSec;
        this.forwardUserCache = forwardUserCache.asMap();
        this.shopInfoParser = shopInfoParser;
        this.itemSearchService = itemSearchService;
        this.adminUserId = adminUserId;
    }

    @TelegramMessage
    public BotApiMethod<?> message(long userId, Message message, DefaultAbsSender client) {
        if (Optional.ofNullable(message.getEntities())
            .map(Collection::stream)
            .filter(stream -> stream.anyMatch(messageEntity ->
                MessageType.findByCode(messageEntity.getType()).filter(MessageType.HASHTAG::equals).isPresent() &&
                    "#bug".equals(messageEntity.getText())
            )).isPresent()) {
            int messageId = message.getMessageId();
            try {
                client.execute(new ForwardMessage(adminUserId, userId, messageId).disableNotification());
                return new SendMessage()
                    .setChatId(userId)
                    .setReplyToMessageId(messageId)
                    .setText("This message was sent to devs");
            } catch (TelegramApiException e) {
                logger.error("Error on send #bug forward", e);
                return new SendMessage()
                    .setChatId(userId)
                    .setReplyToMessageId(messageId)
                    .setText("Something gets wrong");
            }
        }
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(getMessage(itemSearchService.findByCodeThenByName(message.getText())));
    }

    @SuppressWarnings("MethodMayBeStatic")
    @TelegramCommand(commands = "/start", description = "Start command", hidden = true)
    public SendMessage startCommand(long userId) {
        return new SendMessage()
            .setChatId(userId)
            .setText("Now you can press /help to view help");
    }

    @TelegramCommand(commands = "/t_*", description = "Info about item")
    public SendMessage itemInfo(long userId, Message message) {
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(getMessage(itemSearchService.findByCode(message.getText().substring(3))));
    }

    @TelegramCommand(
        commands = { "/craftbook_1", "/craftbook_2", "/craftbook_3" },
        description = "Show specified craftbook"
    )
    public SendMessage showCraftbook(long userId, TelegramMessageCommand command) {
        Optional<String> result = command.getCommand()
            .map(cmd -> cmd.substring(11))
            .flatMap(itemSearchService::getCraftbook);
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(getMessage(result));
    }

    @TelegramCommand(commands = "/a_*", description = "Copy of /t_*", hidden = true)
    public SendMessage itemInfoA(long userId, Message message) {
        return itemInfo(userId, message);
    }

    @TelegramCommand(commands = "/view_*", description = "View recipe")
    public SendMessage recipeView(long userId, Message message) {
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(getMessage(itemSearchService.findRecipeByCode(message.getText().substring(6))));
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

        Optional<ParsedShopInfo> shopInfo = shopInfoParser.parse(message);

        return new SendMessage()
            .setChatId(userId)
            .setText(shopInfo
                .map(s -> "You've sent shop with name='" + s.getShopName() + "'\n" +
                    "for char='" + s.getCharName() + "'\n" +
                    "with command='" + s.getShopCommand() + '\'' +
                    "shop state is: " + s.getShopState().getCode())
                .orElse("Unknown forward")
            );
    }

    @SuppressWarnings("MethodMayBeStatic")
    @TelegramForward
    public SendMessage defaultForward(long userId, User user) {
        logger.info("Accepted unsupported forward data from user: ", userId);
        return new SendMessage()
            .setChatId(userId)
            .setText(String.format("Hi %s, I can't recognize you!",
                user.getFirstName()
            ));
    }

    @SuppressWarnings("MethodMayBeStatic")
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

    @SuppressWarnings("MethodMayBeStatic")
    @TelegramCommand(commands = "/source", description = "Source code", hidden = true)
    public SendMessage source(long userId) {
        return new SendMessage()
            .enableMarkdown(true)
            .setChatId(userId)
            .setText("[here](https://github.com/maratik123/cwshopbot)");
    }

    @SuppressWarnings("MethodMayBeStatic")
    @TelegramHelp
    public String getHelpPrefix() {
        return "To find something just type it's code (or part of name)\n" +
            "Also, you can use the commands from list:\n\n";
    }

    private boolean messageIsStale(Instant forwardTime) {
        return clock.instant().minusSeconds(forwardStaleSec).isAfter(forwardTime);
    }

    private static String getMessage(Optional<String> message) {
        return message.orElse("404 not found");
    }
}
