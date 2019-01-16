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
package name.maratik.cw.cwshopbot.application.storage;

import name.maratik.cw.cwshopbot.mock.MockedTest;
import name.maratik.cw.cwshopbot.model.NavigableYellowPage;
import name.maratik.cw.cwshopbot.model.cwapi.YellowPage;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class YellowPagesStorageTest extends MockedTest {
    @Autowired
    private YellowPagesStorage yellowPagesStorage;

    @Test
    public void shouldSaveAndFetchYellowPages() {
        YellowPage yellowPage = YellowPageUtils.createYellowPage()
            .link("testLink")
            .build();
        yellowPagesStorage.saveYellowPages(ImmutableList.of(yellowPage));

        NavigableYellowPage navigableYellowPage = yellowPagesStorage.findYellowPage("testL");

        assertFalse(navigableYellowPage.getPreviousLink().isPresent());
        assertFalse(navigableYellowPage.getNextLink().isPresent());
        YellowPage fetchedYellowPage = navigableYellowPage.getYellowPage()
            .orElseThrow(() -> new AssertionError("Could not find yellow page"));
        assertEquals(yellowPage, fetchedYellowPage);
    }

    @Test
    public void shouldPreviousLinkNotEmpty() {
        YellowPage yellowPage = YellowPageUtils.createYellowPage()
            .link("testLink")
            .build();
        YellowPage yellowPage1 = YellowPageUtils.createYellowPage()
            .link("testLink1")
            .build();
        YellowPage yellowPage2 = YellowPageUtils.createYellowPage()
            .link("testLink2")
            .build();
        yellowPagesStorage.saveYellowPages(ImmutableList.of(yellowPage, yellowPage1, yellowPage2));

        NavigableYellowPage navigableYellowPage = yellowPagesStorage.findYellowPage("testLink2");

        assertEquals("testLink1", navigableYellowPage.getPreviousLink()
            .orElseThrow(() -> new AssertionError("Could not find previous link"))
        );
    }

    @Test
    public void shouldNextLinkNotEmpty() {
        YellowPage yellowPage = YellowPageUtils.createYellowPage()
            .link("testLink")
            .build();
        YellowPage yellowPage1 = YellowPageUtils.createYellowPage()
            .link("testLink1")
            .build();
        YellowPage yellowPage2 = YellowPageUtils.createYellowPage()
            .link("testLink2")
            .build();
        yellowPagesStorage.saveYellowPages(ImmutableList.of(yellowPage, yellowPage1, yellowPage2));

        NavigableYellowPage navigableYellowPage = yellowPagesStorage.findYellowPage("testLink");

        assertEquals("testLink1", navigableYellowPage.getNextLink()
            .orElseThrow(() -> new AssertionError("Could not find next link"))
        );
    }
}
