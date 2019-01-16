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

import name.maratik.cw.cwshopbot.model.Castle;

import org.junit.Test;

import java.util.Arrays;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class CastleByEmojiTest {
    @Test
    public void shouldAllCastlesMapped() {
        assertThat(
            Arrays.stream(CastleByEmoji.values())
                .map(CastleByEmoji::getCastle)
                .collect(toImmutableList()),
            containsInAnyOrder(
                Castle.values()
            )
        );
    }
}
