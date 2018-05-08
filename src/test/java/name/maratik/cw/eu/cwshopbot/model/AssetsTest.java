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
package name.maratik.cw.eu.cwshopbot.model;

import name.maratik.cw.eu.cwshopbot.mock.MockedTelegramBotsApiTest;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Assets;
import name.maratik.cw.eu.cwshopbot.model.cwasset.CraftableItem;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Item;
import name.maratik.cw.eu.cwshopbot.model.cwasset.ItemLocation;
import name.maratik.cw.eu.cwshopbot.model.cwasset.ItemType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@SpringBootTest
public class AssetsTest extends MockedTelegramBotsApiTest {
    private static final Logger logger = LogManager.getLogger(AssetsTest.class);

    @Autowired
    private Assets assets;

    private Matcher<String> anyOfAllItems;
    private Matcher<String> anyOfCraftableItems;

    @PostConstruct
    public void init() {
        anyOfAllItems = anyOf(assets.getAllItems().keySet().stream()
            .map(Matchers::equalTo)
            .collect(toList())
        );
        anyOfCraftableItems = anyOf(assets.getCraftableItems().keySet().stream()
            .map(Matchers::equalTo)
            .collect(toList())
        );
    }

    @Test
    public void shouldAssetsExists() {
        logger.info("Assets: {}", assets);
        assertNotNull(assets);
    }

    @Test
    public void shouldAllItemsHaveConsistentId() {
        BiConsumer<String, Item> checker = (id, item) -> assertEquals(id, item.getId());
        assets.getAllItems().forEach(checker);
        assets.getCraftableItems().forEach(checker);
        assets.getWearableItems().forEach(checker);
    }

    @Test
    public void shouldAllCraftableItemsInAllItems() {
        assets.getCraftableItems().keySet().forEach(id -> assertThat(id, anyOfAllItems));
    }

    @Test
    public void shouldAllWearableItemsInCraftableItems() {
        assets.getWearableItems().keySet().forEach(id -> assertThat(id, anyOfCraftableItems));
    }

    @Test
    public void shouldAllCraftableItemsInCraftbooks() {
        assertThat(
            assets.getItemsByCraftbook().values().stream()
                .flatMap(Collection::stream)
                .map(Item::getId)
                .collect(toList()),
            containsInAnyOrder(assets.getCraftableItems().values().stream()
                .map(Item::getId)
                .map(Matchers::equalTo)
                .collect(toList())
            )
        );
    }

    @Test
    public void shouldAllEquipmentWearable() {
        assertThat(
            assets.getItemsByItemLocation().get(ItemLocation.EQUIPMENT).stream()
                .map(Item::getId)
                .collect(toList()),
            containsInAnyOrder(assets.getWearableItems().keySet().stream()
                .map(Matchers::equalTo)
                .collect(toList())
            )
        );
    }

    @Test
    public void shouldAllRecipeIngridientsAreItems() {
        assets.getCraftableItems().values().stream()
            .flatMap(craftableItem -> craftableItem.getRecipe().keySet().stream())
            .forEach(id -> assertThat(id, anyOfAllItems));
    }

    @Test
    public void shouldAllRecipeQuantityPositive() {
        assets.getCraftableItems().values().stream()
            .flatMap(craftableItem -> craftableItem.getRecipe().values().stream())
            .forEach(quantity -> assertTrue(quantity > 0));
    }

    @Test
    public void shouldAllWearableConsistentWithItemClass() {
        assets.getWearableItems().values()
            .forEach(wearableItem -> assertEquals(
                wearableItem.getItemType().getItemClass(),
                wearableItem.getBodyPart().getItemClass()
            ));
    }

    @Test
    public void shouldAllManaPositive() {
        assets.getCraftableItems().values().stream()
            .map(CraftableItem::getMana)
            .forEach(mana -> assertTrue(mana > 0));
    }

    @Test
    public void shouldAllWearableParamsNonNegative() {
        assets.getWearableItems().values().stream()
            .flatMap(wearableItem -> Stream.of(
                wearableItem.getAttack(),
                wearableItem.getDefence(),
                wearableItem.getManaboost()
            ))
            .forEach(param -> assertTrue(param >= 0));
    }

    @Test
    public void shouldAtLeastOneWearableEquipmentNotArrowsPackAndToolsParamPositive() {
        Set<ItemType> arrowsPackAndTool = EnumSet.of(ItemType.ARROWS_PACK, ItemType.TOOL);
        assets.getWearableItems().values().stream()
            .filter(wearableItem -> wearableItem.getItemLocation() == ItemLocation.EQUIPMENT)
            .filter(wearableItem -> !arrowsPackAndTool.contains(wearableItem.getItemType()))
            .forEach(wearableItem -> assertTrue(
                wearableItem.getAttack() > 0 ||
                wearableItem.getDefence() > 0 ||
                wearableItem.getManaboost() > 0
            ));
    }
}
