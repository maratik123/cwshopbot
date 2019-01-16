//    cwshopbot
//    Copyright (C) 2019  Marat Bukharov.
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
import name.maratik.cw.cwshopbot.application.service.ItemSearchService;
import name.maratik.cw.cwshopbot.application.service.StatsService;
import name.maratik.cw.cwshopbot.application.service.YellowPagesService;
import name.maratik.cw.cwshopbot.model.ForwardKey;
import name.maratik.cw.cwshopbot.model.NavigableYellowPage;
import name.maratik.cw.cwshopbot.model.parser.ParsedHero;
import name.maratik.cw.cwshopbot.model.parser.ParsedShopEdit;
import name.maratik.cw.cwshopbot.model.parser.ParsedShopInfo;
import name.maratik.cw.cwshopbot.packer.Packer;
import name.maratik.cw.cwshopbot.proto.ReplyData;
import name.maratik.cw.cwshopbot.util.MessageType;
import name.maratik.spring.telegram.annotation.TelegramBot;
import name.maratik.spring.telegram.annotation.TelegramCallbackQuery;
import name.maratik.spring.telegram.annotation.TelegramCommand;
import name.maratik.spring.telegram.annotation.TelegramForward;
import name.maratik.spring.telegram.annotation.TelegramHelp;
import name.maratik.spring.telegram.annotation.TelegramMessage;
import name.maratik.spring.telegram.model.CallbackQueryId;
import name.maratik.spring.telegram.model.TelegramMessageCommand;
import name.maratik.spring.telegram.util.Localizable;

import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import static name.maratik.cw.cwshopbot.util.Emoji.LEFTWARDS_ARROW;
import static name.maratik.cw.cwshopbot.util.Emoji.RIGHTWARDS_ARROW;

import static java.util.Collections.singletonList;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@TelegramBot
@Log4j2
public class ShopController extends Localizable {

    public static final int PAGE_SIZE = 30;
    public static final String VIEW_PREFIX = "/view_";
    public static final String RVIEW_PREFIX = "/rview_";
    public static final String A_PREFIX = "/a_";
    public static final String T_PREFIX = "/t_";
    public static final String CRAFTBOOK_PREFIX = "/craftbook_";
    public static final String SHOP_SEARCH_PREFIX = "/shop_";
    public static final String SHOP_COMMAND_PREFIX = "/ws_";
    public static final String YP_PREFIX = "/yp_";
    public static final String PATTERN_COMMAND_SUFFIX = "*";

    private final Clock clock;
    private final Duration forwardStale;
    private final ConcurrentMap<ForwardKey, Long> forwardUserCache;
    private final CWParser<ParsedShopInfo> shopInfoParser;
    private final CWParser<ParsedShopEdit> shopEditParser;
    private final CWParser<ParsedHero> heroParser;
    private final ItemSearchService itemSearchService;
    private final long devUserId;
    private final String devUserName;
    private final String cwUserName;
    private final StatsService statsService;
    private final YellowPagesService yellowPagesService;

    public ShopController(Clock clock, Duration forwardStale,
                          @ForwardUser Cache<ForwardKey, Long> forwardUserCache,
                          CWParser<ParsedShopInfo> shopInfoParser, CWParser<ParsedShopEdit> shopEditParser,
                          CWParser<ParsedHero> heroParser, ItemSearchService itemSearchService,
                          @Value("${name.maratik.cw.cwshopbot.dev}") long devUserId,
                          @Value("${name.maratik.cw.cwshopbot.dev.username}") String devUserName,
                          @Value("${cwusername}") String cwUserName, StatsService statsService,
                          YellowPagesService yellowPagesService
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
        this.cwUserName = cwUserName;
        this.statsService = statsService;
        this.yellowPagesService = yellowPagesService;
    }

    @TelegramMessage
    public BotApiMethod<?> message(long userId, Message message, DefaultAbsSender client, User user) {
        statsService.updateStats("shop.message", user);
        if (Optional.ofNullable(message.getEntities())
            .map(Collection::stream)
            .filter(stream -> stream.anyMatch(messageEntity ->
                MessageType.findByCode(messageEntity.getType()) == MessageType.HASHTAG &&
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
                    .setText(t("ShopController.MESSAGE.TO_DEVS"));
            } catch (Exception e) {
                log.error("Error on send #bug forward", e);
                return new SendMessage()
                    .setChatId(userId)
                    .setReplyToMessageId(messageId)
                    .setText(t("ShopController.MESSAGE.TO_DEVS.FAILED", devUserName));
            }
        }
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(getMessage(itemSearchService.findByCodeThenByName(message.getText())));
    }

    @TelegramCommand(commands = "/start", description = "#{@loc.t('ShopController.COMMAND.START.DESC')}", hidden = true)
    public SendMessage startCommand(long userId, User user) {
        statsService.updateStats("shop.start", user);
        return new SendMessage()
            .setChatId(userId)
            .setText(t("ShopController.COMMAND.START.REPLY"));
    }

    @TelegramCommand(commands = "/auth", description = "#{@loc.t('ShopController.COMMAND.AUTH.DESC')}", hidden = true)
    public SendMessage authCommand(long userId, User user) {
        statsService.updateStats("shop.auth", user);
        return new SendMessage()
            .setChatId(userId)
            .setText(t("ShopController.COMMAND.AUTH.REPLY", cwUserName));
    }

    @TelegramCommand(
        commands = "/register",
        description = "#{@loc.t('ShopController.COMMAND.REGISTER.DESC')}",
        hidden = true
    )
    public SendMessage registerCommand(long userId, User user) {
        statsService.updateStats("shop.register", user);
        return new SendMessage()
            .setChatId(userId)
            .setText(t("ShopController.COMMAND.REGISTER.REPLY"));
    }

    @TelegramCommand(
        commands = T_PREFIX + PATTERN_COMMAND_SUFFIX,
        description = "#{@loc.t('ShopController.COMMAND.T.DESC')}"
    )
    public SendMessage itemInfo(long userId, Message message, User user) {
        statsService.updateStats("shop.t", user);
        return itemInfo(userId, message, T_PREFIX.length());
    }

    @TelegramCommand(
        commands = A_PREFIX + PATTERN_COMMAND_SUFFIX,
        description = "#{@loc.t('ShopController.COMMAND.T.DESC')}",
        hidden = true
    )
    public SendMessage itemInfoA(long userId, Message message, User user) {
        statsService.updateStats("shop.a", user);
        return itemInfo(userId, message, A_PREFIX.length());
    }

    @TelegramCommand(
        commands = {
            CRAFTBOOK_PREFIX + '1', CRAFTBOOK_PREFIX + '2', CRAFTBOOK_PREFIX + '3',
            CRAFTBOOK_PREFIX + '4', CRAFTBOOK_PREFIX + '5'
        },
        description = "#{@loc.t('ShopController.COMMAND.CRAFTBOOK.DESC')}"
    )
    public SendMessage showCraftbook(long userId, Message message, User user) {
        statsService.updateStats("shop.craftbook", user);
        Optional<String> result = itemSearchService.getCraftbook(getCommandSuffix(message, CRAFTBOOK_PREFIX.length()));
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(getMessage(result));
    }

    @TelegramCommand(
        commands = VIEW_PREFIX + PATTERN_COMMAND_SUFFIX,
        description = "#{@loc.t('ShopController.COMMAND.VIEW.DESC')}"
    )
    public SendMessage recipeView(long userId, Message message, User user) {
        statsService.updateStats("shop.view", user);
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(getMessage(itemSearchService.findRecipeByCode(getCommandSuffix(message, VIEW_PREFIX.length()))));
    }

    @TelegramCommand(
        commands = RVIEW_PREFIX + PATTERN_COMMAND_SUFFIX,
        description = "#{@loc.t('ShopController.COMMAND.RVIEW.DESC')}"
    )
    public SendMessage reverseRecipeSearch(long userId, Message message, User user) {
        statsService.updateStats("shop.rview", user);
        return new SendMessage()
            .setChatId(userId)
            .enableMarkdown(true)
            .setText(getMessage(
                itemSearchService.findRecipeByIncludedItem(getCommandSuffix(message, RVIEW_PREFIX.length()))
            ));
    }

    @TelegramForward("${cwuserid}")
    public SendMessage forward(
        Update update, String messageText, User user, long userId, Instant forwardTime, Message message
    ) {
        statsService.updateStats("shop.forward", user);
        log.info("Accepted incoming forward data: {}", messageText);

        if (messageIsStale(forwardTime)) {
            log.info("Forwarded stale update: {}", update);
            return new SendMessage()
                .setChatId(userId)
                .setText(t("ShopController.FORWARD.STALE"));
        }

        Long previousUserForwarded = forwardUserCache.putIfAbsent(new ForwardKey(message), userId);

        if (previousUserForwarded != null) {
            return new SendMessage()
                .setChatId(userId)
                .setText(t(
                    previousUserForwarded != userId
                        ? "ShopController.FORWARD.OTHER_S"
                        : "ShopController.FORWARD.ALREADY_SEEN"
                ));
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
                .setText(t("ShopController.FORWARD.UNSUPPORTED"));
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

    @TelegramForward
    public SendMessage defaultForward(long userId, User user) {
        statsService.updateStats("shop.forward.default", user);
        log.info("Accepted unsupported forward data from user: ", userId);
        return new SendMessage()
            .setChatId(userId)
            .setText(t("ShopController.FORWARD.UNSUPPORTED", user.getFirstName()));
    }

    @TelegramCommand(commands = "/license", description = "#{@loc.t('ShopController.COMMAND.LICENSE.DESC')}")
    public SendMessage license(long userId, User user) {
        statsService.updateStats("shop.license", user);
        return new SendMessage()
            .enableMarkdown(true)
            .setChatId(userId)
            .setText(t("ShopController.COMMAND.LICENSE.REPLY"));
    }

    @TelegramCommand(
        commands = "/source",
        description = "#{@loc.t('ShopController.COMMAND.SOURCE.DESC')}",
        hidden = true
    )
    public SendMessage source(long userId, User user) {
        statsService.updateStats("shop.source", user);
        return new SendMessage()
            .enableMarkdown(true)
            .setChatId(userId)
            .setText(t("ShopController.COMMAND.SOURCE.REPLY", "https://github.com/maratik123/cwshopbot"));
    }

    @TelegramCommand(
        commands = YP_PREFIX + PATTERN_COMMAND_SUFFIX,
        description = "#{@loc.t('ShopController.COMMAND.YP.DESC')}"
    )
    public SendMessage workshop(long userId, Message message, User user) {
        statsService.updateStats("shop.ws", user);
        return yellowPagesHelper(userId, getCommandSuffix(message, YP_PREFIX.length()));
    }

    @TelegramCommand(
        commands = "/yp",
        description = "#{@loc.t('ShopController.COMMAND.YP.DESC')}"
    )
    public SendMessage yellowPages(long userId, User user, TelegramMessageCommand command) {
        statsService.updateStats("shop.yellow.pages", user);
        return yellowPagesHelper(userId, command.getArgument().orElse(null));
    }

    private SendMessage yellowPagesHelper(long userId, String key) {
        Optional<Map.Entry<NavigableYellowPage, String>> response = yellowPagesService.formattedYellowPages(key);
        SendMessage sendMessage = new SendMessage()
            .enableMarkdown(true)
            .setChatId(userId)
            .setText(getMessage(response.map(Map.Entry::getValue)));
        return response
            .map(Map.Entry::getKey)
            .map(ShopController::getKeysForYellowPages)
            .map(sendMessage::setReplyMarkup)
            .orElse(sendMessage);
    }

    @TelegramCallbackQuery
    public AnswerCallbackQuery callbackQuery(long userId, User user, String data, CallbackQueryId queryId,
                                             DefaultAbsSender client, Message message, Update update) {
        statsService.updateStats("shop.callback.query", user);
        if (message != null) {
            Packer.unpackData(data)
                .filter(pagedRequest -> pagedRequest.getRequestType() == ReplyData.RequestType.YELLOW_PAGES)
                .map(ReplyData.PagedRequest::getQuery)
                .flatMap(yellowPagesService::formattedYellowPages)
                .ifPresent(yellowPages -> {
                    statsService.updateStats("shop.callback.query.yellow.pages", user);
                    try {
                        client.execute(new EditMessageText()
                            .enableMarkdown(true)
                            .setChatId(userId)
                            .setMessageId(message.getMessageId())
                            .setText(yellowPages.getValue())
                            .setReplyMarkup(getKeysForYellowPages(yellowPages.getKey()))
                        );
                    } catch (TelegramApiException e) {
                        log.error("Can not process execute on request: {}", update, e);
                    }
                });
        }

        return new AnswerCallbackQuery()
            .setCallbackQueryId(queryId.getId());
    }

    @SuppressWarnings("ReturnOfNull")
    private static InlineKeyboardMarkup getKeysForYellowPages(NavigableYellowPage navigableYellowPage) {
        if (navigableYellowPage == null) {
            return null;
        }
        ImmutableList.Builder<InlineKeyboardButton> keyboardBuilder = ImmutableList.builder();
        navigableYellowPage.getPreviousLink()
            .flatMap(ShopController::backwardYellowPagesButton)
            .ifPresent(keyboardBuilder::add);
        navigableYellowPage.getNextLink()
            .flatMap(ShopController::forwardYellowPagesButton)
            .ifPresent(keyboardBuilder::add);
        List<InlineKeyboardButton> buttons = keyboardBuilder.build();
        return buttons.isEmpty()
            ? null
            : new InlineKeyboardMarkup().setKeyboard(singletonList(buttons));
    }

    private static Optional<InlineKeyboardButton> backwardYellowPagesButton(String key) {
        return Packer.packData(
            ReplyData.PagedRequest.newBuilder()
                .setRequestType(ReplyData.RequestType.YELLOW_PAGES)
                .setQuery(key)
                .build(),
            64
        )
            .map(data -> new InlineKeyboardButton()
                .setCallbackData(data)
                .setText(LEFTWARDS_ARROW)
            );
    }

    private static Optional<InlineKeyboardButton> forwardYellowPagesButton(String key) {
        return Packer.packData(
            ReplyData.PagedRequest.newBuilder()
                .setRequestType(ReplyData.RequestType.YELLOW_PAGES)
                .setQuery(key)
                .build(),
            64
        )
            .map(data -> new InlineKeyboardButton()
                .setCallbackData(data)
                .setText(RIGHTWARDS_ARROW)
            );
    }

    @TelegramHelp
    public String getHelpPrefix() {
        return t("ShopController.HELP.PREFIX") + "\n\n";
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

    private String getMessage(Optional<String> message) {
        return message.orElseGet(() -> t("ShopController.404"));
    }

    private static String getCommandSuffix(Message message, int prefixLen) {
        return message.getText().substring(prefixLen);
    }
}
