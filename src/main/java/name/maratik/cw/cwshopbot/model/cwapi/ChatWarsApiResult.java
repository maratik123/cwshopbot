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
package name.maratik.cw.cwshopbot.model.cwapi;

import name.maratik.cw.cwshopbot.util.EnumWithCode;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@RequiredArgsConstructor
public enum ChatWarsApiResult implements EnumWithCode {
    OK("Ok"),
    BAD_AMOUNT("BadAmount"),
    BAD_CURRENCY("BadCurrency"),
    BAD_FORMAT("BadFormat"),
    ACTION_NOT_FOUND("ActionNotFound"),
    NO_SUCH_USER("NoSuchUser"),
    NOT_REGISTERED("NotRegistered"),
    INVALID_CODE("InvalidCode"),
    TRY_AGAIN("TryAgain"),
    AUTHORIZATION_FAILED("AuthorizationFailed"),
    INSUFFICIENT_FUNDS("InsufficientFunds"),
    INVALID_TOKEN("InvalidToken"),
    FORBIDDEN("Forbidden"),
    BATTLE_IS_NEAR("BattleIsNear"),
    USER_IS_BUSY("UserIsBusy"),
    PROHIBITED_ITEM("ProhibitedItem"),
    NO_OFFERS_FOUND("NoOffersFound"),
    NO_OFFERS_FOUND_BY_PRICE("NoOffersFoundByPrice"),
    UNKNOWN("unknown");

    @Getter(onMethod_ = {@JsonValue})
    private final String code;
    private static final Map<String, ChatWarsApiResult> cache = Util.createCache(values());

    public static ChatWarsApiResult findByCode(String code) {
        return cache.getOrDefault(code, UNKNOWN);
    }
}
