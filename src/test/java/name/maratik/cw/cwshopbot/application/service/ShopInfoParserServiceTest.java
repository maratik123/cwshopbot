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
package name.maratik.cw.cwshopbot.application.service;

import name.maratik.cw.cwshopbot.mock.MockMessage;
import name.maratik.cw.cwshopbot.mock.MockMessageEntity;
import name.maratik.cw.cwshopbot.mock.MockedTest;
import name.maratik.cw.cwshopbot.model.Castle;
import name.maratik.cw.cwshopbot.model.Profession;
import name.maratik.cw.cwshopbot.model.ShopState;
import name.maratik.cw.cwshopbot.model.cwasset.Assets;
import name.maratik.cw.cwshopbot.model.parser.ParsedShopInfo;

import com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;

import static name.maratik.cw.cwshopbot.utils.SamePropertyValuesAsExcept.samePropertyValuesAsExcept;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ShopInfoParserServiceTest extends MockedTest {

    @Autowired
    private CWParser<ParsedShopInfo> shopInfoParser;

    @Autowired
    private Assets assets;

    @Test
    public void shouldParseShopInfoMessage() {
        Message mockedMessage = new MockMessage( "" +
            "Welcome, to the Drunken Master Shack #56.\n" +
            "Forester SM 730/730\uD83D\uDCA7the Blacksmith from Moonlight\n" +
            '\n' +
            "Shack is closed.\n" +
            '\n' +
            "Silver alloy, 15\uD83D\uDCA7 1\uD83D\uDCB0 /ws_lBR02_25\n" +
            "Steel arrows pack, 120\uD83D\uDCA7 1\uD83D\uDCB0 /ws_lBR02_511\n" +
            '\n' +
            "Good day. /ws_lBR02",
            ImmutableList.of(
                new MockMessageEntity("bold", 16, 24),
                new MockMessageEntity("bold", 42, 11),
                new MockMessageEntity("bold", 103, 6),
                new MockMessageEntity("bot_command", 135, 12),
                new MockMessageEntity("bot_command", 177, 13),
                new MockMessageEntity("bot_command", 202, 9)
            )
        );
        ParsedShopInfo parsedShopInfo = shopInfoParser.parse(mockedMessage)
            .orElseThrow(AssertionError::new);

        ParsedShopInfo expected = ParsedShopInfo.builder()
            .setShopName("Drunken Master Shack")
            .setShopType("Shack")
            .setShopNumber(56)
            .setCharName("Forester SM")
            .setCurrentMana(730)
            .setMaxMana(730)
            .setProfession(Profession.BLACKSMITH)
            .setCastle(Castle.MOONLIGHT)
            .setShopState(ShopState.CLOSED)
            .setShopCommand("/ws_lBR02")
            .addShopLine(ParsedShopInfo.ShopLine.builder()
                .setItem(assets.getCraftableItems().get("25"))
                .setMana(15)
                .setPrice(1)
                .setCraftCommand("/ws_lBR02_25")
                .build()
            )
            .addShopLine(ParsedShopInfo.ShopLine.builder()
                .setItem(assets.getWearableItems().get("511"))
                .setMana(120)
                .setPrice(1)
                .setCraftCommand("/ws_lBR02_511")
                .build()
            )
            .build();

        assertThat(parsedShopInfo, samePropertyValuesAsExcept(expected, "shopLines"));
        assertThat(parsedShopInfo.getShopLines(), contains(expected.getShopLines().stream()
            .map(Matchers::samePropertyValuesAs)
            .collect(toImmutableList())
        ));
    }
}
