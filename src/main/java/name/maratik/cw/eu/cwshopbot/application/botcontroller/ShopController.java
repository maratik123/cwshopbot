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

import name.maratik.cw.eu.cwshopbot.application.config.ForwardUser;
import name.maratik.cw.eu.cwshopbot.application.service.CWParser;
import name.maratik.cw.eu.cwshopbot.application.service.ChatWarsAuthService;
import name.maratik.cw.eu.cwshopbot.application.service.ItemSearchService;
import name.maratik.cw.eu.cwshopbot.model.ForwardKey;
import name.maratik.cw.eu.cwshopbot.model.parser.ParsedHero;
import name.maratik.cw.eu.cwshopbot.model.parser.ParsedShopEdit;
import name.maratik.cw.eu.cwshopbot.model.parser.ParsedShopInfo;
import name.maratik.cw.eu.cwshopbot.util.Localizable;
import name.maratik.cw.eu.cwshopbot.util.MessageType;
import name.maratik.cw.eu.spring.annotation.TelegramBot;
import name.maratik.cw.eu.spring.annotation.TelegramCommand;
import name.maratik.cw.eu.spring.annotation.TelegramForward;
import name.maratik.cw.eu.spring.annotation.TelegramHelp;
import name.maratik.cw.eu.spring.annotation.TelegramMessage;

import com.google.common.cache.Cache;
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

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import static name.maratik.cw.eu.spring.TelegramBotService.PATTERN_COMMAND_SUFFIX;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@TelegramBot
public class ShopController extends Localizable {
    private static final Logger logger = LogManager.getLogger(ShopController.class);

    public static final String VIEW_PREFIX = "/view_";
    public static final String RVIEW_PREFIX = "/rview_";
    public static final String A_PREFIX = "/a_";
    public static final String T_PREFIX = "/t_";
    public static final String CRAFTBOOK_PREFIX = "/craftbook_";
    public static final String SHOP_SEARCH_PREFIX = "/shop_";
    public static final String SHOP_COMMAND_PREFIX = "/ws_";

    private final Clock clock;
    private final Duration forwardStale;
    private final ConcurrentMap<ForwardKey, Long> forwardUserCache;
    private final CWParser<ParsedShopInfo> shopInfoParser;
    private final CWParser<ParsedShopEdit> shopEditParser;
    private final CWParser<ParsedHero> heroParser;
    private final ItemSearchService itemSearchService;
    private final long devUserId;
    private final String devUserName;
    private final ChatWarsAuthService chatWarsAuthService;
    private final String cwUserName;

    public ShopController(Clock clock, Duration forwardStale,
                          @ForwardUser Cache<ForwardKey, Long> forwardUserCache,
                          CWParser<ParsedShopInfo> shopInfoParser, CWParser<ParsedShopEdit> shopEditParser,
                          CWParser<ParsedHero> heroParser, ItemSearchService itemSearchService,
                          @Value("${name.maratik.cw.eu.cwshopbot.dev}") long devUserId,
                          @Value("${name.maratik.cw.eu.cwshopbot.dev.username}") String devUserName,
                          ChatWarsAuthService chatWarsAuthService,
                          @Value("${cwusername}") String cwUserName
    ) {
        this.clock = clock;
        this.forwardStale = forwardStale;
        this.forwardUserCache = forwardUserCache.asMap();
        this.shopInfoParser = shopInfoParser;
        this.shopEditParser = shopEditParser;
        this.heroParser = heroParser;
        this.itemSearchService = itemSearchService;
        this.devUserId = devUserId;
        this.devUserName = devUserName;
        this.chatWarsAuthService = chatWarsAuthService;
        this.cwUserName = cwUserName;
    }

    @TelegramMessage
    public BotApiMethod<?> message(long userId, Message message, DefaultAbsSender client) {
        if (Optional.ofNullable(message.getEntities())
            .map(Collection::stream)
            .filter(stream -> stream.anyMatch(messageEntity ->
                MessageType.findByCode(messageEntity.getType()).filter(MessageType.HASHTAG::equals).isPresent() &&
                    "#bug".equals(messageEntity.getText())
            )).isPresent()) {
            Integer messageId = message.getMessageId();
            try {
                if (messageId == null) {
                    throw new Exception("Unsupported message");
                }
                client.execute(new ForwardMessage(devUserId, userId, messageId).disableNotification());
                return new SendMessage()
                    .setChatId(userId)
                    .setReplyToMessageId(messageId)
                    .setText("This message was sent to devs");
            } catch (Exception e) {
                logger.error("Error on send #bug forward", e);
                return new SendMessage()
                    .setChatId(userId)
                    .setReplyToMessageId(messageId)
                    .setText("Something gets wrong. Please, send it directly to @" + devUserName);
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

    @TelegramCommand(commands = "/auth", description = "Authenticate within CW", hidden = true)
    public SendMessage authCommand(long userId) {
        return new SendMessage()
            .setChatId(userId)
            .setText("Now, send me the authentication code from " + cwUserName);
    }

    @SuppressWarnings("MethodMayBeStatic")
    @TelegramCommand(commands = "/register", description = "Register your shop", hidden = true)
    public SendMessage registerCommand(long userId) {
        return new SendMessage()
            .setChatId(userId)
            .setText("To start with registering your shop, please, send me the fresh forward of /hero info");
    }

    @TelegramCommand(commands = T_PREFIX + PATTERN_COMMAND_SUFFIX, description = "Info about item")
    public SendMessage itemInfo(long userId, Message message) {
        return itemInfo(userId, message, T_PREFIX.length());
    }

    @TelegramCommand(
        commands = A_PREFIX + PATTERN_COMMAND_SUFFIX,
        description = "Copy of " + T_PREFIX + PATTERN_COMMAND_SUFFIX,
        hidden = true
    )
    public SendMessage itemInfoA(long userId, Message message) {
        return itemInfo(userId, message, A_PREFIX.length());
    }

    @TelegramCommand(
        commands = { CRAFTBOOK_PREFIX + '1', CRAFTBOOK_PREFIX + '2', CRAFTBOOK_PREFIX + '3' },
        description = "Show specified craftbook"
    )
    public SendMessage showCraftbook(long userId, Message message) {
        Optional<String> result = itemSearchService.getCraftbook(getCommandSuffix(message, CRAFTBOOK_PREFIX.length()));
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(getMessage(result));
    }

    @TelegramCommand(commands = VIEW_PREFIX + PATTERN_COMMAND_SUFFIX, description = "View recipe")
    public SendMessage recipeView(long userId, Message message) {
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(getMessage(itemSearchService.findRecipeByCode(getCommandSuffix(message, VIEW_PREFIX.length()))));
    }

    @TelegramCommand(commands = RVIEW_PREFIX + PATTERN_COMMAND_SUFFIX, description = "Find recipes for item included")
    public SendMessage reverseRecipeSearch(long userId, Message message) {
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(getMessage(
                itemSearchService.findRecipeByIncludedItem(getCommandSuffix(message, RVIEW_PREFIX.length()))
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

        Optional<ParsedShopInfo> shopInfo = shopInfoParser.parse(message);
        Optional<ParsedShopEdit> shopEdit = shopInfo.isPresent()
            ? Optional.empty()
            : shopEditParser.parse(message);
        Optional<ParsedHero> hero = shopInfo.isPresent() || shopEdit.isPresent()
            ? Optional.empty()
            : heroParser.parse(message);

        if (!(shopInfo.isPresent() || shopEdit.isPresent() || hero.isPresent())) {
            return new SendMessage()
                .setChatId(userId)
                .setText("Unsupported forward");
        }

        return new SendMessage()
            .setChatId(userId)
            .setText(shopInfo
                .map(s -> "You've sent the shop " + s)
                .orElseGet(() -> shopEdit.map(s -> "You've sent the shop edit " + s)
                    .orElseGet(() -> hero.map(s -> "Hero forward: " + s)
                        .orElse("")
                    )
                )
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
        return clock.instant().minus(forwardStale).isAfter(forwardTime);
    }

    private SendMessage itemInfo(long userId, Message message, int prefixLen) {
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(getMessage(itemSearchService.findByCode(getCommandSuffix(message, prefixLen))));
    }

    private static String getMessage(Optional<String> message) {
        return message.orElse("404 not found");
    }

    private static String getCommandSuffix(Message message, int prefixLen) {
        return message.getText().substring(prefixLen);
    }
}
