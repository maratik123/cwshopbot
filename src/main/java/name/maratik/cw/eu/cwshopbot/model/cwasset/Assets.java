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
package name.maratik.cw.eu.cwshopbot.model.cwasset;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collector;

import static com.google.common.collect.Maps.toImmutableEnumMap;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class Assets {
    private final Map<String, Item> allItems;
    private final Map<ItemLocation, Set<Item>> itemsByItemLocation;
    private final Map<BodyPart, Set<WearableItem>> itemsByBodyPart;
    private final Map<ItemType, Set<WearableItem>> itemsByItemType;
    private final Map<Craftbook, Set<CraftableItem>> itemsByCraftbook;
    private final Map<String, CraftableItem> craftableItems;
    private final Map<String, WearableItem> wearableItems;

    public Assets(Map<String, Item> allItems) {
        this.allItems = Objects.requireNonNull(allItems);
        Map<ItemLocation, ImmutableSet.Builder<Item>> itemsByItemLocationBuilder = new EnumMap<>(ItemLocation.class);
        Map<BodyPart, ImmutableSet.Builder<WearableItem>> itemsByBodyPartBuilder = new EnumMap<>(BodyPart.class);
        Map<ItemType, ImmutableSet.Builder<WearableItem>> itemsByItemTypeBuilder = new EnumMap<>(ItemType.class);
        Map<Craftbook, ImmutableSet.Builder<CraftableItem>> itemsByCraftbookBuilder = new EnumMap<>(Craftbook.class);
        ImmutableMap.Builder<String, CraftableItem> craftableItemsBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<String, WearableItem> wearableItemsBuilder = ImmutableMap.builder();
        allItems.forEach((id, item) -> item.apply(new Item.Visitor() {
            @Override
            public void visit(Item item) {
                itemsByItemLocationBuilder.computeIfAbsent(
                    item.getItemLocation(),
                    itemLocation -> ImmutableSet.builder()
                ).add(item);
            }

            @Override
            public void visit(CraftableItem craftableItem) {
                visit((Item) craftableItem);

                itemsByCraftbookBuilder.computeIfAbsent(
                    craftableItem.getCraftbook(),
                    craftbook -> ImmutableSet.builder()
                ).add(craftableItem);
                craftableItemsBuilder.put(id, craftableItem);
            }

            @Override
            public void visit(WearableItem wearableItem) {
                visit((CraftableItem) wearableItem);

                itemsByBodyPartBuilder.computeIfAbsent(
                    wearableItem.getBodyPart(),
                    bodyPart -> ImmutableSet.builder()
                ).add(wearableItem);
                itemsByItemTypeBuilder.computeIfAbsent(
                    wearableItem.getItemType(),
                    itemType -> ImmutableSet.builder()
                ).add(wearableItem);
                wearableItemsBuilder.put(id, wearableItem);
            }
        }));
        itemsByItemLocation = itemsByItemLocationBuilder.entrySet().stream()
            .collect(createImmutableMapCollector());
        itemsByBodyPart = itemsByBodyPartBuilder.entrySet().stream()
            .collect(createImmutableMapCollector());
        itemsByItemType = itemsByItemTypeBuilder.entrySet().stream()
            .collect(createImmutableMapCollector());
        itemsByCraftbook = itemsByCraftbookBuilder.entrySet().stream()
            .collect(createImmutableMapCollector());
        craftableItems = craftableItemsBuilder.build();
        wearableItems = wearableItemsBuilder.build();
    }

    public Map<String, Item> getAllItems() {
        return allItems;
    }

    public Map<ItemLocation, Set<Item>> getItemsByItemLocation() {
        return itemsByItemLocation;
    }

    public Map<BodyPart, Set<WearableItem>> getItemsByBodyPart() {
        return itemsByBodyPart;
    }

    public Map<ItemType, Set<WearableItem>> getItemsByItemType() {
        return itemsByItemType;
    }

    public Map<Craftbook, Set<CraftableItem>> getItemsByCraftbook() {
        return itemsByCraftbook;
    }

    public Map<String, CraftableItem> getCraftableItems() {
        return craftableItems;
    }

    public Map<String, WearableItem> getWearableItems() {
        return wearableItems;
    }

    @Override
    public String toString() {
        return "Assets{" +
            "allItems=" + allItems +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    private static <K extends Enum<K>, V, T extends Map.Entry<K, ImmutableSet.Builder<V>>>
    Collector<T, ?, ImmutableMap<K, Set<V>>> createImmutableMapCollector() {
        return toImmutableEnumMap(Map.Entry::getKey, entry -> entry.getValue().build());
    }

    public static class Builder {
        private final ImmutableMap.Builder<String, Item> itemsBuilder = ImmutableMap.builder();

        public Builder putItem(Item item) {
            itemsBuilder.put(item.getId(), item);
            return this;
        }

        public Assets build() {
            return new Assets(itemsBuilder.build());
        }
    }
}
