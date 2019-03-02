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
package name.maratik.cw.cwshopbot.model.cwasset;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.Maps.toImmutableEnumMap;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(onlyExplicitlyIncluded = true)
public class Assets {
    @NonNull
    @ToString.Include
    private final Map<String, Item> allItems;
    @NonNull
    private final Map<ItemLocation, Set<Item>> itemsByItemLocation;
    @NonNull
    private final Map<InventorySlot, Set<WearableItem>> itemsByInventorySlot;
    @NonNull
    private final Map<ItemType, Set<WearableItem>> itemsByItemType;
    @NonNull
    private final Map<Craftbook, Set<CraftableItem>> itemsByCraftbook;
    @NonNull
    private final Map<String, CraftableItem> craftableItems;
    @NonNull
    private final Map<String, WearableItem> wearableItems;
    @NonNull
    private final Map<String, Set<CraftableItem>> craftableItemsByRecipe;
    @NonNull
    private final Map<String, Item> itemsByName;
    @NonNull
    private final Map<String, Item> itemsByNameLowerCase;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Collection<? extends Item> allItemList;

        public Builder setAllItemList(Collection<? extends Item> items) {
            allItemList = items;
            return this;
        }

        public Assets build() {
            Map<String, Item> allItems = allItemList.stream()
                .collect(toImmutableMap(Item::getId, t -> t));
            Map<ItemLocation, ImmutableSet.Builder<Item>> itemsByItemLocationBuilder = new EnumMap<>(ItemLocation.class);
            Map<InventorySlot, ImmutableSet.Builder<WearableItem>> itemsByInventorySlotBuilder = new EnumMap<>(InventorySlot.class);
            Map<ItemType, ImmutableSet.Builder<WearableItem>> itemsByItemTypeBuilder = new EnumMap<>(ItemType.class);
            Map<Craftbook, ImmutableSet.Builder<CraftableItem>> itemsByCraftbookBuilder = new EnumMap<>(Craftbook.class);
            ImmutableMap.Builder<String, CraftableItem> craftableItemsBuilder = ImmutableMap.builder();
            ImmutableMap.Builder<String, WearableItem> wearableItemsBuilder = ImmutableMap.builder();
            ImmutableMap.Builder<String, Item> itemsByNameBuilder = ImmutableMap.builder();
            ImmutableMap.Builder<String, Item> itemsByNameLowerCaseBuilder = ImmutableMap.builder();
            allItems.forEach((id, item) -> item.apply(
                new BuilderFiller(id, itemsByItemLocationBuilder, itemsByCraftbookBuilder, itemsByInventorySlotBuilder,
                    itemsByItemTypeBuilder, craftableItemsBuilder, wearableItemsBuilder, itemsByNameBuilder,
                    itemsByNameLowerCaseBuilder
                )
            ));
            Map<String, CraftableItem> craftableItems = craftableItemsBuilder.build();
            Map<String, Set<CraftableItem>> craftableItemsByRecipe = ImmutableMap.copyOf(craftableItems.values().stream()
                .flatMap(craftableItem -> craftableItem.getRecipe().keySet().stream()
                    .map(recipePart -> new SimpleImmutableEntry<>(recipePart, craftableItem))
                ).collect(groupingBy(
                    Map.Entry::getKey,
                    mapping(Map.Entry::getValue, toImmutableSet())
                ))
            );
            return new Assets(
                allItems,
                itemsByItemLocationBuilder.entrySet().stream()
                    .collect(createImmutableEnumMapCollector()),
                itemsByInventorySlotBuilder.entrySet().stream()
                    .collect(createImmutableEnumMapCollector()),
                itemsByItemTypeBuilder.entrySet().stream()
                    .collect(createImmutableEnumMapCollector()),
                itemsByCraftbookBuilder.entrySet().stream()
                    .collect(createImmutableEnumMapCollector()),
                craftableItems,
                wearableItemsBuilder.build(),
                craftableItemsByRecipe,
                itemsByNameBuilder.build(),
                itemsByNameLowerCaseBuilder.build());
        }

        private static <K extends Enum<K>, V, T extends Map.Entry<K, ImmutableSet.Builder<V>>>
        Collector<T, ?, ImmutableMap<K, Set<V>>> createImmutableEnumMapCollector() {
            return toImmutableEnumMap(Map.Entry::getKey, entry -> entry.getValue().build());
        }

        @RequiredArgsConstructor
        private static class BuilderFiller implements Item.Visitor<Void> {
            private final String id;
            private final Map<ItemLocation, ImmutableSet.Builder<Item>> itemsByItemLocationBuilder;
            private final Map<Craftbook, ImmutableSet.Builder<CraftableItem>> itemsByCraftbookBuilder;
            private final Map<InventorySlot, ImmutableSet.Builder<WearableItem>> itemsByInventorySlotBuilder;
            private final Map<ItemType, ImmutableSet.Builder<WearableItem>> itemsByItemTypeBuilder;
            private final ImmutableMap.Builder<String, CraftableItem> craftableItemsBuilder;
            private final ImmutableMap.Builder<String, WearableItem> wearableItemsBuilder;
            private final ImmutableMap.Builder<String, Item> itemsByNameBuilder;
            private final ImmutableMap.Builder<String, Item> itemsByNameLowerCaseBuilder;

            @Override
            public Void visit(Item item) {
                itemsByItemLocationBuilder.computeIfAbsent(
                    item.getItemLocation(),
                    itemLocation -> ImmutableSet.builder()
                ).add(item);
                itemsByNameBuilder.put(item.getName(), item);
                itemsByNameLowerCaseBuilder.put(item.getLowerName(), item);
                return null;
            }

            @Override
            public Void visit(CraftableItem craftableItem) {
                Item.Visitor.super.visit(craftableItem);

                itemsByCraftbookBuilder.computeIfAbsent(
                    craftableItem.getCraftbook(),
                    craftbook -> ImmutableSet.builder()
                ).add(craftableItem);
                craftableItemsBuilder.put(id, craftableItem);
                return null;
            }

            @Override
            public Void visit(WearableItem wearableItem) {
                Item.Visitor.super.visit(wearableItem);

                itemsByInventorySlotBuilder.computeIfAbsent(
                    wearableItem.getInventorySlot(),
                    inventorySlot -> ImmutableSet.builder()
                ).add(wearableItem);
                itemsByItemTypeBuilder.computeIfAbsent(
                    wearableItem.getItemType(),
                    itemType -> ImmutableSet.builder()
                ).add(wearableItem);
                wearableItemsBuilder.put(id, wearableItem);
                return null;
            }
        }
    }
}
