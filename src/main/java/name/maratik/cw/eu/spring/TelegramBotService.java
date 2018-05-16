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
package name.maratik.cw.eu.spring;

import name.maratik.cw.eu.spring.annotation.TelegramCommand;
import name.maratik.cw.eu.spring.annotation.TelegramForward;
import name.maratik.cw.eu.spring.model.TelegramBotCommand;
import name.maratik.cw.eu.spring.model.TelegramHandler;
import name.maratik.cw.eu.spring.model.TelegramMessageCommand;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static name.maratik.cw.eu.cwshopbot.util.Utils.optionalOf;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public abstract class TelegramBotService implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(TelegramBotService.class);
    public static final String PATTERN_COMMAND_SUFFIX = "*";
    private static final int PATTERN_COMMAND_SUFFIX_LEN = PATTERN_COMMAND_SUFFIX.length();

    private final Map<OptionalLong, Handlers> handlers = new HashMap<>();
    private final ConfigurableBeanFactory beanFactory;

    private final Map<Type, BiFunction<TelegramMessageCommand, Update, ?>> argumentMapper;

    public TelegramBotService(TelegramBotsApi api, ConfigurableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;

        BiFunction<TelegramMessageCommand, Update, Long> userIdExtractor = (telegramMessageCommand, update) ->
            update.getMessage().getFrom().getId().longValue();

        argumentMapper = ImmutableMap.<Type, BiFunction<TelegramMessageCommand, Update, ?>>builder()
            .put(Update.class, (telegramMessageCommand, update) -> update)
            .put(TelegramMessageCommand.class, (telegramMessageCommand, update) -> telegramMessageCommand)
            .put(String.class, (telegramMessageCommand, update) -> telegramMessageCommand.getArgument().orElse(null))
            .put(TelegramBotsApi.class, (telegramMessageCommand, update) -> api)
            .put(TelegramBotService.class, (telegramMessageCommand, update) -> this)
            .put(DefaultAbsSender.class, (telegramMessageCommand, update) -> getClient())
            .put(Message.class, (telegramMessageCommand, update) -> update.getMessage())
            .put(User.class, (telegramMessageCommand, update) -> update.getMessage().getFrom())
            .put(long.class, userIdExtractor)
            .put(Long.class, userIdExtractor)
            .put(Instant.class, ((telegramMessageCommand, update) -> {
                Message message = update.getMessage();
                return Instant.ofEpochSecond(Optional.ofNullable(message.getForwardDate()).orElse(message.getDate()));
            }))
            .build();
    }

    @SuppressWarnings("WeakerAccess")
    public Optional<BotApiMethod<?>> updateProcess(Update update) {
        logger.debug("Update {} received", update);
        if (update.getMessage() == null) {
            return Optional.empty();
        }
        TelegramMessageCommand command = new TelegramMessageCommand(update);
        Optional<TelegramHandler> optionalCommandHandler;
        OptionalLong userKey = optionalOf(update.getMessage().getChatId());
        Handlers handlers = getOrDefault(userKey);

        if (command.getForwardedFrom().isPresent()) {
            optionalCommandHandler = Optional.ofNullable(
                handlers.getForwardHandlerList().getOrDefault(command.getForwardedFrom().getAsLong(),
                    handlers.getDefaultForwardHandler()
                )
            );
        } else {
            optionalCommandHandler = command.getCommand().map(cmd -> handlers.getCommandList().get(cmd));
            if (!optionalCommandHandler.isPresent()) {
                if (command.getCommand().isPresent()) {
                    optionalCommandHandler = handlers.getPatternCommandList().entrySet().stream()
                        .filter(entry -> command.getCommand().get().startsWith(entry.getKey()))
                        .map(Map.Entry::getValue)
                        .findFirst();
                }
                if (!optionalCommandHandler.isPresent()) {
                    optionalCommandHandler = Optional.ofNullable(handlers.getDefaultMessageHandler());
                }
            }
        }

        logger.debug("Command handler: {}", optionalCommandHandler);

        return optionalCommandHandler.flatMap(commandHandler -> {
            try {
                Method method = commandHandler.getMethod();
                Object[] arguments = makeArgumentList(method, command, update);

                if (commandHandler.getTelegramCommand().filter(TelegramCommand::isHelp).isPresent()) {
                    sendHelpList(update, userKey);
                } else {
                    Class<?> methodReturnType = method.getReturnType();
                    logger.debug("Derived method return type: {}", methodReturnType);
                    if (methodReturnType == void.class || methodReturnType == Void.class) {
                        method.invoke(commandHandler.getBean(), arguments);
                    } else if (methodReturnType != null &&
                        BotApiMethod.class.isAssignableFrom(methodReturnType)) {
                        return Optional.ofNullable((BotApiMethod<?>) method.invoke(commandHandler.getBean(), arguments));
                    } else {
                        logger.error("Unsupported handler '{}'", commandHandler);
                    }
                }
            } catch (Exception e) {
                logger.error("Could not process update: {}", update, e);
            }
            return Optional.empty();
        });
    }

    private void sendHelpList(Update update, OptionalLong userKey) throws TelegramApiException {
        getClient().execute(new SendMessage()
            .setChatId(update.getMessage().getChatId())
            .setText(buildHelpMessage(userKey))
        );
    }

    private static final Set<String> LAST_COMMANDS_IN_HELP = ImmutableSet.of("/license", "/help");

    private String buildHelpMessage(OptionalLong userKey) {
        StringBuilder sb = new StringBuilder();
        String prefixHelpMessage = getOrDefault(userKey).getPrefixHelpMessage();
        if (prefixHelpMessage != null) {
            sb.append(prefixHelpMessage);
        }
        getCommandList(userKey)
            .sorted(
                Comparator.comparing(
                    TelegramBotCommand::getCommand,
                    Comparator.comparing(LAST_COMMANDS_IN_HELP::contains)
                ).thenComparing(TelegramBotCommand::getCommand)
            )
            .forEach(method -> sb
                .append(method.getCommand())
                .append(' ')
                .append(method.getDescription())
                .append('\n')
            );
        return sb.toString();
    }

    @SuppressWarnings("WeakerAccess")
    public Stream<TelegramBotCommand> getCommandList(OptionalLong userKey) {
        return Stream.concat(
            getOrDefault(userKey).getCommandList().entrySet().stream()
                .filter(entry -> !entry.getValue().getTelegramCommand().map(TelegramCommand::hidden).orElse(true))
                .map(entry -> new TelegramBotCommand(
                    entry.getKey(),
                    entry.getValue().getTelegramCommand().map(TelegramCommand::description).orElse("")
                )),
            getOrDefault(userKey).getPatternCommandList().entrySet().stream()
                .filter(entry -> !entry.getValue().getTelegramCommand().map(TelegramCommand::hidden).orElse(true))
                .map(entry -> new TelegramBotCommand(
                    entry.getKey() + PATTERN_COMMAND_SUFFIX,
                    entry.getValue().getTelegramCommand().map(TelegramCommand::description).orElse("")
                ))
        );
    }

    public abstract DefaultAbsSender getClient();

    private Object[] makeArgumentList(Method method, TelegramMessageCommand telegramMessageCommand, Update update) {
        return Arrays.stream(method.getGenericParameterTypes())
            .map(type -> argumentMapper.getOrDefault(type, (t, u) -> null))
            .map(mapper -> mapper.apply(telegramMessageCommand, update))
            .toArray();
    }

    @SuppressWarnings("WeakerAccess")
    public void addHandler(Object bean, Method method, OptionalLong userId) {
        TelegramCommand command = AnnotatedElementUtils.findMergedAnnotation(method, TelegramCommand.class);
        if (command != null) {
            for (String cmd : command.commands()) {
                TelegramHandler telegramHandler = new TelegramHandler(bean, method, command);
                if (cmd.endsWith(PATTERN_COMMAND_SUFFIX)) {
                    createOrGet(userId).getPatternCommandList()
                        .put(cmd.substring(0, cmd.length() - PATTERN_COMMAND_SUFFIX_LEN), telegramHandler);
                } else {
                    createOrGet(userId).getCommandList().put(cmd, telegramHandler);
                }
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void addDefaultMessageHandler(Object bean, Method method, OptionalLong userId) {
        createOrGet(userId).setDefaultMessageHandler(new TelegramHandler(bean, method, null));
    }

    @SuppressWarnings("WeakerAccess")
    public void addForwardMessageHandler(Object bean, Method method, OptionalLong userId) {
        TelegramForward forward = AnnotatedElementUtils.findMergedAnnotation(method, TelegramForward.class);
        if (forward != null) {
            String[] fromArr = forward.from();
            if (fromArr.length == 0) {
                createOrGet(userId).setDefaultForwardHandler(new TelegramHandler(bean, method, null));
            } else {
                for (String from : fromArr) {
                    String parsedFromStr = beanFactory.resolveEmbeddedValue(from);
                    if (parsedFromStr == null) {
                        throw new RuntimeException("NPE in " + from);
                    }
                    for (String fromValue : parsedFromStr.split(",")) {
                        Long parsedFrom = Long.valueOf(fromValue);
                        createOrGet(userId).getForwardHandlerList()
                            .put(parsedFrom, new TelegramHandler(bean, method, null));
                    }
                }
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void addHelpMethod(OptionalLong userKey) {
        try {
            Method helpMethod = getClass().getMethod("helpMethod");
            TelegramCommand command = AnnotatedElementUtils.findMergedAnnotation(helpMethod, TelegramCommand.class);
            if (command != null) {
                for (String cmd : command.commands()) {
                    createOrGet(userKey).getCommandList()
                        .put(cmd, new TelegramHandler(this, helpMethod, command));
                }
            }
        } catch (Exception e) {
            logger.error("Could not add help method", e);
        }
    }

    @SuppressWarnings("WeakerAccess")
    @TelegramCommand(commands = "/help", isHelp = true, description = "This help")
    public void helpMethod() {
    }

    @Override
    public void close() {
    }

    @SuppressWarnings("WeakerAccess")
    public void addHelpPrefixMethod(Object bean, Method method, OptionalLong userId) {
        try {
            createOrGet(userId).setPrefixHelpMessage((String) method.invoke(bean));
        } catch (Exception e) {
            logger.error("Can not get help prefix", e);
        }
    }

    private Handlers getOrDefault(OptionalLong key) {
        if (!handlers.containsKey(key)) {
            return handlers.get(OptionalLong.empty());
        }
        return handlers.get(key);
    }

    private Handlers createOrGet(OptionalLong key) {
        return handlers.computeIfAbsent(key, Handlers::create);
    }

    private static class Handlers {
        private final Map<String, TelegramHandler> commandList = new HashMap<>();
        private final Map<String, TelegramHandler> patternCommandList = new HashMap<>();
        private final Map<Long, TelegramHandler> forwardHandlerList = new HashMap<>();
        private TelegramHandler defaultMessageHandler;
        private TelegramHandler defaultForwardHandler;
        private String prefixHelpMessage;

        private static Handlers create(@SuppressWarnings("unused") OptionalLong key) {
            return new Handlers();
        }

        private Map<String, TelegramHandler> getCommandList() {
            return commandList;
        }

        private Map<String, TelegramHandler> getPatternCommandList() {
            return patternCommandList;
        }

        private Map<Long, TelegramHandler> getForwardHandlerList() {
            return forwardHandlerList;
        }

        private TelegramHandler getDefaultMessageHandler() {
            return defaultMessageHandler;
        }

        private void setDefaultMessageHandler(TelegramHandler defaultMessageHandler) {
            this.defaultMessageHandler = defaultMessageHandler;
        }

        private TelegramHandler getDefaultForwardHandler() {
            return defaultForwardHandler;
        }

        private void setDefaultForwardHandler(TelegramHandler defaultForwardHandler) {
            this.defaultForwardHandler = defaultForwardHandler;
        }

        private String getPrefixHelpMessage() {
            return prefixHelpMessage;
        }

        private void setPrefixHelpMessage(String prefixHelpMessage) {
            this.prefixHelpMessage = prefixHelpMessage;
        }
    }
}
