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

import com.google.common.collect.ImmutableMap;
import name.maratik.cw.eu.spring.annotation.TelegramCommand;
import name.maratik.cw.eu.spring.annotation.TelegramForward;
import name.maratik.cw.eu.spring.model.TelegramBotCommand;
import name.maratik.cw.eu.spring.model.TelegramHandler;
import name.maratik.cw.eu.spring.model.TelegramMessageCommand;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public abstract class TelegramBotService implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(TelegramBotService.class);

    private final Map<String, TelegramHandler> commandList = new LinkedHashMap<>();
    private final Map<String, TelegramHandler> patternCommandList = new LinkedHashMap<>();
    private final Map<Long, TelegramHandler> forwardHandlerList = new HashMap<>();
    private final ConfigurableBeanFactory beanFactory;
    private TelegramHandler defaultMessageHandler;
    private TelegramHandler defaultForwardHandler;
    private String prefixHelpMessage;

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

        addHelpMethod();
    }

    @SuppressWarnings("WeakerAccess")
    public Optional<BotApiMethod<?>> updateProcess(Update update) {
        logger.debug("Update {} received", update);
        if (update.getMessage() == null) {
            return Optional.empty();
        }
        TelegramMessageCommand command = new TelegramMessageCommand(update);
        Optional<TelegramHandler> optionalCommandHandler;
        if (command.getForwardedFrom().isPresent()) {
            optionalCommandHandler = Optional.ofNullable(
                forwardHandlerList.getOrDefault(command.getForwardedFrom().get(), defaultForwardHandler)
            );
        } else {
            optionalCommandHandler = command.getCommand().map(commandList::get);
            if (!optionalCommandHandler.isPresent()) {
                if (command.getCommand().isPresent()) {
                    optionalCommandHandler = patternCommandList.entrySet().stream()
                        .filter(entry -> command.getCommand().get().startsWith(entry.getKey()))
                        .map(Map.Entry::getValue)
                        .findFirst();
                }
                if (!optionalCommandHandler.isPresent()) {
                    optionalCommandHandler = Optional.ofNullable(defaultMessageHandler);
                }
            }
        }

        logger.debug("Command handler: {}", optionalCommandHandler);

        return optionalCommandHandler.flatMap(commandHandler -> {
            try {
                Method method = commandHandler.getMethod();
                Object[] arguments = makeArgumentList(method, command, update);

                if (commandHandler.getTelegramCommand().filter(TelegramCommand::isHelp).isPresent()) {
                    sendHelpList(update);
                } else {
                    Type methodReturnType = method.getReturnType();
                    logger.debug("Derived method return type: {}", methodReturnType);
                    if (methodReturnType == void.class) {
                        method.invoke(commandHandler.getBean(), arguments);
                    } else if (methodReturnType != null &&
                        BotApiMethod.class.isAssignableFrom((Class<?>) methodReturnType)) {
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

    private void sendHelpList(Update update) throws TelegramApiException {
        getClient().execute(new SendMessage()
            .setChatId(update.getMessage().getChatId())
            .setText(buildHelpMessage())
        );
    }

    private String buildHelpMessage() {
        StringBuilder sb = new StringBuilder();
        if (prefixHelpMessage != null) {
            sb.append(prefixHelpMessage);
        }
        getCommandList()
            .sorted(Comparator.comparing(
                (TelegramBotCommand command) -> command.getCommand().equals("/license") ||
                    command.getCommand().equals("/help")
                ).thenComparing(TelegramBotCommand::getCommand))
            .forEach(method -> sb
                .append(method.getCommand())
                .append(' ')
                .append(method.getDescription())
                .append('\n')
            );
        return sb.toString();
    }

    @SuppressWarnings("WeakerAccess")
    public Stream<TelegramBotCommand> getCommandList() {
        return Stream.concat(
            commandList.entrySet().stream()
                .filter(entry -> !entry.getValue().getTelegramCommand().map(TelegramCommand::hidden).orElse(true))
                .map(entry -> new TelegramBotCommand(
                    entry.getKey(),
                    entry.getValue().getTelegramCommand().map(TelegramCommand::description).orElse("")
                )),
            patternCommandList.entrySet().stream()
                .filter(entry -> !entry.getValue().getTelegramCommand().map(TelegramCommand::hidden).orElse(true))
                .map(entry -> new TelegramBotCommand(
                    entry.getKey() + '*',
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

    public void addHandler(Object bean, Method method) {
        TelegramCommand command = AnnotatedElementUtils.findMergedAnnotation(method, TelegramCommand.class);
        if (command != null) {
            for (String cmd : command.commands()) {
                //noinspection ObjectAllocationInLoop
                TelegramHandler telegramHandler = new TelegramHandler(bean, method, command);
                if (cmd.endsWith("*")) {
                    patternCommandList.put(cmd.substring(0, cmd.length() - 1), telegramHandler);
                } else {
                    commandList.put(cmd, telegramHandler);
                }
            }
        }
    }

    public void addDefaultMessageHandler(Object bean, Method method) {
        defaultMessageHandler = new TelegramHandler(bean, method, null);
    }

    public void addForwardMessageHandler(Object bean, Method method) {
        TelegramForward forward = AnnotatedElementUtils.findMergedAnnotation(method, TelegramForward.class);
        if (forward != null) {
            String[] fromArr = forward.from();
            if (fromArr.length == 0) {
                defaultForwardHandler = new TelegramHandler(bean, method, null);
            } else {
                for (String from : fromArr) {
                    Long parsedFrom = Long.valueOf(Objects.requireNonNull(beanFactory.resolveEmbeddedValue(from)));
                    //noinspection ObjectAllocationInLoop
                    forwardHandlerList.put(parsedFrom, new TelegramHandler(bean, method, null));
                }
            }
        }
    }

    private void addHelpMethod() {
        try {
            Method helpMethod = getClass().getMethod("helpMethod");
            TelegramCommand command = AnnotatedElementUtils.findMergedAnnotation(helpMethod, TelegramCommand.class);
            if (command != null) {
                for (String cmd : command.commands()) {
                    //noinspection ObjectAllocationInLoop
                    commandList.put(cmd, new TelegramHandler(this, helpMethod, command));
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

    public void addHelpPrefixMethod(Object bean, Method method) {
        try {
            prefixHelpMessage = (String) method.invoke(bean);
        } catch (Exception e) {
            logger.error("Can not get help prefix", e);
        }
    }
}
