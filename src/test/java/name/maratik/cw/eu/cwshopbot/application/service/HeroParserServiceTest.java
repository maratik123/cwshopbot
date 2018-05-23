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
package name.maratik.cw.eu.cwshopbot.application.service;

import name.maratik.cw.eu.cwshopbot.mock.MockMessage;
import name.maratik.cw.eu.cwshopbot.mock.MockMessageEntity;
import name.maratik.cw.eu.cwshopbot.mock.MockedTest;
import name.maratik.cw.eu.cwshopbot.model.Castle;
import name.maratik.cw.eu.cwshopbot.model.parser.ParsedHero;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.objects.Message;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class HeroParserServiceTest extends MockedTest {
    @Autowired
    private CWParser<ParsedHero> heroParser;

    @Test
    public void testParser() {
        Message mockedMessage = new MockMessage("" +
            "\uD83C\uDF11[ETC]Forester SM of Moonlight Castle\n" +
            "\uD83C\uDFC5Level: 31\n" +
            "\u2694Atk: 56 \uD83D\uDEE1Def: 115\n" +
            "\uD83D\uDD25Exp: 60204/63979\n" +
            "\uD83D\uDD0BStamina: 2/5\n" +
            "\uD83D\uDCA7Mana: 740/740\n" +
            "\uD83D\uDCB0118 \uD83D\uDC5D9\n" +
            "\uD83D\uDCDAExpertise: \uD83D\uDCD5\uD83D\uDCD7\n" +
            "\uD83C\uDFDBClass info: /class\n" +
            '\n' +
            '\n' +
            '\n' +
            "\uD83C\uDFBDEquipment +14\u2694+53\uD83D\uDEE1:\n" +
            "Club +2\u2694 +2\uD83D\uDEE1\n" +
            "Steel dagger +3\u2694\n" +
            "Clarity Circlet +2\u2694 +12\uD83D\uDEE1\n" +
            "Clarity Bracers +1\u2694 +9\uD83D\uDEE1\n" +
            "Clarity Robe +4\u2694 +20\uD83D\uDEE1\n" +
            "Clarity Shoes +1\u2694 +9\uD83D\uDEE1\n" +
            "Royal Guard Cape +1\u2694 +1\uD83D\uDEE1\n" +
            '\n' +
            "\uD83C\uDF92Bag: 13/15 /inv\n" +
            "\uD83D\uDCE6Warehouse: 2238 /stock",
            ImmutableList.of(
                new MockMessageEntity("bold", 2, 16),
                new MockMessageEntity("bot_command", 163, 6),
                new MockMessageEntity("bold", 173, 22),
                new MockMessageEntity("bot_command", 364, 4),
                new MockMessageEntity("bot_command", 387, 6)
            )
        );
        ParsedHero parsedHero = heroParser.parse(mockedMessage)
            .orElseThrow(AssertionError::new);

        ParsedHero expected = ParsedHero.builder()
            .setCharName("Forester SM")
            .setCastle(Castle.MOONLIGHT)
            .setGuildAbbrev("ETC")
            .build();

        assertThat(parsedHero, samePropertyValuesAs(expected));
    }
}
