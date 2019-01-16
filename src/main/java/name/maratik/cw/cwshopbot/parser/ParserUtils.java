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
package name.maratik.cw.cwshopbot.parser;

import name.maratik.cw.cwshopbot.model.cwasset.Item;

import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

import static name.maratik.cw.cwshopbot.application.botcontroller.ShopController.SHOP_COMMAND_PREFIX;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Log4j2
public class ParserUtils {
    public static void verifyItem(Item item, int mana) {
        item.apply(CraftableItemVerifier.getInstance());
        item.apply(new ManaCostVerifier(mana));
    }

    public static String extractShopCodeFromShopCommand(String shopCommand) {
        return shopCommand.substring(SHOP_COMMAND_PREFIX.length());
    }

    public static <T> Optional<T> catchParseErrors(ParseAction<T> parseAction, Message message) {
        try {
            return parseAction.action();
        } catch (ParseCancellationException e) {
            log.error("Failed to parse message {}", message, e);
            if (e.getCause() instanceof RecognitionException) {
                RecognitionException recognitionException = (RecognitionException) e.getCause();
                log.error("Expected tokens: {}, offended one: {}",
                    recognitionException.getExpectedTokens(), recognitionException.getOffendingToken()
                );
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Failed to parse message {}", message, e);
            return Optional.empty();
        }
    }
}
