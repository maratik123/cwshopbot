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
package name.maratik.cw.cwshopbot.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum MessageType implements EnumWithCode {
    MENTION("mention"),
    HASHTAG("hashtag"),
    BOT_COMMAND("bot_command"),
    URL("url"),
    EMAIL("email"),
    BOLD("bold", "*", "*"),
    ITALIC("italic"),
    CODE("code"),
    PRE("pre"),
    TEXT_LINK("text_link"),
    TEXT_MENTION("text_mention"),
    TEXT("text");

    @Getter(onMethod_ = {@JsonValue})
    private final String code;
    @Getter
    private final Optional<String> prefix;
    @Getter
    private final Optional<String> postfix;
    private static final Map<String, MessageType> cache = Util.createCache(values());

    MessageType(String code) {
        this.code = code;
        this.prefix = Optional.empty();
        this.postfix = Optional.empty();
    }

    MessageType(String code, String prefix, String postfix) {
        this.code = code;
        this.prefix = Optional.of(prefix);
        this.postfix = Optional.of(postfix);
    }

    @JsonCreator
    public static MessageType findByCode(String code) {
        return cache.getOrDefault(code, TEXT);
    }

}
