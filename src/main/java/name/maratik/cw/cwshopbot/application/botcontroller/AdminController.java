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
import name.maratik.cw.cwshopbot.model.PagedResponse;
import name.maratik.cw.cwshopbot.model.parser.ParsedHero;
import name.maratik.cw.cwshopbot.model.parser.ParsedShopEdit;
import name.maratik.cw.cwshopbot.model.parser.ParsedShopInfo;
import name.maratik.cw.cwshopbot.packer.Packer;
import name.maratik.cw.cwshopbot.proto.ReplyData;
import name.maratik.spring.telegram.TelegramBotService;
import name.maratik.spring.telegram.annotation.TelegramBot;
import name.maratik.spring.telegram.annotation.TelegramCommand;
import name.maratik.spring.telegram.model.CallbackQueryId;
import name.maratik.spring.telegram.model.TelegramMessageCommand;

import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static name.maratik.cw.cwshopbot.util.Emoji.LEFTWARDS_ARROW;
import static name.maratik.cw.cwshopbot.util.Emoji.RIGHTWARDS_ARROW;

import static java.util.Collections.singletonList;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@TelegramBot("${name.maratik.cw.cwshopbot.admin}")
@Log4j2
public class AdminController extends ShopController {

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
                           @Value("${cwusername}") String cwUserName, YellowPagesService yellowPagesService
    ) {
        super(clock, forwardStale, forwardUserCache, shopInfoParser, shopEditParser, heroParser, itemSearchService,
            devUserId, devUserName, cwUserName, statsService, yellowPagesService);
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
            log.error("Can not send goodbye", e);
        }
    }

    @TelegramCommand(commands = "/stats", description = "#{@loc.t('AdminController.STATS.COMMON')}")
    public SendMessage getStat(long userId, User user) {
        statsService.updateStats("admin.stats", user);
        return new SendMessage()
            .setChatId(userId)
            .setText(statsService.getStats());
    }

    @TelegramCommand(commands = "/stats_cache", description = "#{@loc.t('AdminController.STATS.CACHES')}")
    public SendMessage getCacheStats(long userId, User user) {
        statsService.updateStats("admin.stats.cache", user);
        return new SendMessage()
            .setChatId(userId)
            .setText(statsService.getCacheStats());
    }

    @TelegramCommand(commands = "/stats_commands", description = "#{@loc.t('AdminController.STATS.COMMANDS')}")
    public SendMessage getCommandsStats(long userId, User user) {
        statsService.updateStats("admin.stats.commands", user);
        return new SendMessage()
            .setChatId(userId)
            .setText(statsService.getCommandStats());
    }

    @TelegramCommand(commands = "/stats_users", description = "#{@loc.t('AdminController.STATS.USERS')}")
    public SendMessage getUsersStats(long userId, User user) {
        statsService.updateStats("admin.stats.users", user);
        PagedResponse<String> pagedResponse = getUsersStatsPagedHelper(0);
        return new SendMessage()
            .enableMarkdown(true)
            .setChatId(userId)
            .setText(pagedResponse.getResponse())
            .setReplyMarkup(getKeysForStatsUsers(pagedResponse, 0));
    }

    private static InlineKeyboardMarkup getKeysForStatsUsers(PagedResponse<String> pagedResponse, int currentPage) {
        ImmutableList.Builder<InlineKeyboardButton> keyboardBuilder = ImmutableList.builder();
        backwardButton(currentPage).ifPresent(keyboardBuilder::add);
        forwardButton(pagedResponse, currentPage).ifPresent(keyboardBuilder::add);
        List<InlineKeyboardButton> buttons = keyboardBuilder.build();
        return buttons.isEmpty()
            ? null
            : new InlineKeyboardMarkup()
                .setKeyboard(singletonList(buttons));
    }

    private static Optional<InlineKeyboardButton> backwardButton(int currentPage) {
        if (currentPage <= 0) {
            return Optional.empty();
        }
        return Packer.packData(
            ReplyData.PagedRequest.newBuilder()
                .setPage(currentPage - 1)
                .setRequestType(ReplyData.RequestType.STATS_USERS)
                .build(),
            64
        )
            .map(data -> new InlineKeyboardButton()
                .setCallbackData(data)
                .setText(LEFTWARDS_ARROW)
            );
    }

    private static Optional<InlineKeyboardButton> forwardButton(PagedResponse<String> pagedResponse, int currentPage) {
        if ((currentPage + 1) * PAGE_SIZE > pagedResponse.getCount()) {
            return Optional.empty();
        }
        return Packer.packData(
            ReplyData.PagedRequest.newBuilder()
                .setPage(currentPage + 1)
                .setRequestType(ReplyData.RequestType.STATS_USERS)
                .build(),
            64
        )
            .map(data -> new InlineKeyboardButton()
                .setCallbackData(data)
                .setText(RIGHTWARDS_ARROW)
            );
    }

    @Override
    public AnswerCallbackQuery callbackQuery(long userId, User user, String data, CallbackQueryId queryId,
                                                  DefaultAbsSender client, Message message, Update update) {
        if (message != null) {
            Packer.unpackData(data)
                .filter(pagedRequest -> pagedRequest.getRequestType() == ReplyData.RequestType.STATS_USERS)
                .ifPresent(pagedRequest -> {
                    statsService.updateStats("admin.stats.users.paged", user);
                    try {
                        int currentPage = pagedRequest.getPage();
                        PagedResponse<String> pagedResponse = getUsersStatsPagedHelper(currentPage);
                        client.execute(new EditMessageText()
                            .enableMarkdown(true)
                            .setChatId(userId)
                            .setMessageId(message.getMessageId())
                            .setText(pagedResponse.getResponse())
                            .setReplyMarkup(getKeysForStatsUsers(pagedResponse, currentPage))
                        );
                    } catch (TelegramApiException e) {
                        log.error("Can not process execute on request: {}", update, e);
                    }
                });
        }

        return super.callbackQuery(userId, user, data, queryId, client, message, update);
    }

    private PagedResponse<String> getUsersStatsPagedHelper(int page) {
        return statsService.getUsersStats(page * PAGE_SIZE, PAGE_SIZE);
    }

    @TelegramCommand(commands = "/send", description = "#{@loc.t('AdminController.SEND_MESSAGE')}")
    public SendMessage sendMessage(TelegramMessageCommand messageCommand, long userId, DefaultAbsSender client, User user) {
        statsService.updateStats("admin.send", user);
        String[] args = messageCommand.getArgument()
            .map(arg -> arg.split(" ", 2))
            .filter(arr -> arr.length == 2)
            .orElseGet(() -> new String[]{Long.toString(userId), t("AdminController.SEND_MESSAGE.WRONG_COMMAND")});

        try {
            client.execute(new SendMessage()
                .setChatId(args[0])
                .setText(args[1])
            );
            return new SendMessage()
                .setChatId(userId)
                .setText(t("AdminController.SEND_MESSAGE.SUCCESS"));
        } catch (TelegramApiException e) {
            log.error("Send message wrong", e);
            return new SendMessage()
                .setChatId(userId)
                .setText(t("AdminController.SEND_MESSAGE.ERROR", e.getMessage()));
        }
    }
}
