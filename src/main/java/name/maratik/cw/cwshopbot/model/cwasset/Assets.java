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
package name.maratik.cw.cwshopbot.model.cwasset;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
public class Assets {
    private final Map<String, Item> allItems;
    private final Map<ItemLocation, Set<Item>> itemsByItemLocation;
    private final Map<InventorySlot, Set<WearableItem>> itemsByInventorySlot;
    private final Map<ItemType, Set<WearableItem>> itemsByItemType;
    private final Map<Book, Set<CraftableItem>> itemsByBook;
    private final Map<String, CraftableItem> craftableItems;
    private final Map<String, WearableItem> wearableItems;
    private final Map<String, Set<CraftableItem>> craftableItemsByRecipe;
    private final Map<String, Item> itemsByName;
    private final Map<String, Item> itemsByNameLowerCase;

    private Assets(Map<String, Item> allItems, Map<ItemLocation, Set<Item>> itemsByItemLocation,
                   Map<InventorySlot, Set<WearableItem>> itemsByInventorySlot, Map<ItemType, Set<WearableItem>> itemsByItemType,
                   Map<Book, Set<CraftableItem>> itemsByBook, Map<String, CraftableItem> craftableItems,
                   Map<String, WearableItem> wearableItems, Map<String, Set<CraftableItem>> craftableItemsByRecipe,
                   Map<String, Item> itemsByName, Map<String, Item> itemsByNameLowerCase) {
        this.allItems = Objects.requireNonNull(allItems, "allItems");
        this.itemsByItemLocation = Objects.requireNonNull(itemsByItemLocation, "itemsByItemLocation");
        this.itemsByInventorySlot = Objects.requireNonNull(itemsByInventorySlot, "itemsByInventorySlot");
        this.itemsByItemType = Objects.requireNonNull(itemsByItemType, "itemsByItemType");
        this.itemsByBook = Objects.requireNonNull(itemsByBook, "itemsByBook");
        this.craftableItems = Objects.requireNonNull(craftableItems, "craftableItems");
        this.wearableItems = Objects.requireNonNull(wearableItems, "wearableItems");
        this.craftableItemsByRecipe = Objects.requireNonNull(craftableItemsByRecipe, "craftableItemsByRecipe");
        this.itemsByName = Objects.requireNonNull(itemsByName, "itemsByName");
        this.itemsByNameLowerCase = Objects.requireNonNull(itemsByNameLowerCase, "itemsByNameLowerCase");
    }

    public Map<String, Item> getAllItems() {
        return allItems;
    }

    public Map<ItemLocation, Set<Item>> getItemsByItemLocation() {
        return itemsByItemLocation;
    }

    public Map<InventorySlot, Set<WearableItem>> getItemsByInventorySlot() {
        return itemsByInventorySlot;
    }

    public Map<ItemType, Set<WearableItem>> getItemsByItemType() {
        return itemsByItemType;
    }

    public Map<Book, Set<CraftableItem>> getItemsByBook() {
        return itemsByBook;
    }

    public Map<String, CraftableItem> getCraftableItems() {
        return craftableItems;
    }

    public Map<String, WearableItem> getWearableItems() {
        return wearableItems;
    }

    public Map<String, Set<CraftableItem>> getCraftableItemsByRecipe() {
        return craftableItemsByRecipe;
    }

    public Map<String, Item> getItemsByName() {
        return itemsByName;
    }

    public Map<String, Item> getItemsByNameLowerCase() {
        return itemsByNameLowerCase;
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

    public static class Builder {
        private Collection<? extends Item> allItemList;

        public Builder setAllItemList(Collection<? extends Item> items) {
            allItemList = items;
            return this;
        }

        public Assets build() {
            Map<String, Item> allItems = allItemList.stream()
                .collect(toImmutableMap(Item::getId, item -> item));
            Map<ItemLocation, ImmutableSet.Builder<Item>> itemsByItemLocationBuilder = new EnumMap<>(ItemLocation.class);
            Map<InventorySlot, ImmutableSet.Builder<WearableItem>> itemsByInventorySlotBuilder = new EnumMap<>(InventorySlot.class);
            Map<ItemType, ImmutableSet.Builder<WearableItem>> itemsByItemTypeBuilder = new EnumMap<>(ItemType.class);
            ImmutableMap.Builder<Craftbook, ImmutableSet.Builder<CraftableItem>> itemsByBookBuilder = ImmutableMap.builder();
            ImmutableMap.Builder<String, CraftableItem> craftableItemsBuilder = ImmutableMap.builder();
            ImmutableMap.Builder<String, WearableItem> wearableItemsBuilder = ImmutableMap.builder();
            ImmutableMap.Builder<String, Item> itemsByNameBuilder = ImmutableMap.builder();
            ImmutableMap.Builder<String, Item> itemsByNameLowerCaseBuilder = ImmutableMap.builder();
            allItems.forEach((id, item) -> item.apply(
                new BuilderFiller(id, itemsByItemLocationBuilder, itemsByBookBuilder, itemsByInventorySlotBuilder,
                    itemsByItemTypeBuilder, craftableItemsBuilder, wearableItemsBuilder, itemsByNameBuilder,
                    itemsByNameLowerCaseBuilder
                )
            ));
            Map<String, CraftableItem> craftableItems = craftableItemsBuilder.build();
            Map<String, Set<CraftableItem>> craftableItemsByRecipe = ImmutableMap.copyOf(craftableItems.values().stream()
                .flatMap(craftableItem -> craftableItem.getRecipe().keySet().stream()
                    .map(recipePart -> new AbstractMap.SimpleImmutableEntry<>(recipePart, craftableItem))
                ).collect(groupingBy(
                    Map.Entry::getKey,
                    mapping(Map.Entry::getValue, toImmutableSet())
                ))
            );
            return new Assets(
                allItems,
                itemsByItemLocationBuilder.entrySet().stream()
                    .collect(createImmutableMapCollector()),
                itemsByInventorySlotBuilder.entrySet().stream()
                    .collect(createImmutableMapCollector()),
                itemsByItemTypeBuilder.entrySet().stream()
                    .collect(createImmutableMapCollector()),
                itemsByBookBuilder.build(),
                craftableItems,
                wearableItemsBuilder.build(),
                craftableItemsByRecipe,
                itemsByNameBuilder.build(),
                itemsByNameLowerCaseBuilder.build());
        }

        private static <K extends Enum<K>, V, T extends Map.Entry<K, ImmutableSet.Builder<V>>>
        Collector<T, ?, ImmutableMap<K, Set<V>>> createImmutableMapCollector() {
            return toImmutableEnumMap(Map.Entry::getKey, entry -> entry.getValue().build());
        }

        private static class BuilderFiller implements Item.Visitor<Void> {
            private final String id;
            private final Map<ItemLocation, ImmutableSet.Builder<Item>> itemsByItemLocationBuilder;
            private final ImmutableMap.Builder<Book, ImmutableSet.Builder<CraftableItem>> itemsByBookBuilder;
            private final Map<InventorySlot, ImmutableSet.Builder<WearableItem>> itemsByInventorySlotBuilder;
            private final Map<ItemType, ImmutableSet.Builder<WearableItem>> itemsByItemTypeBuilder;
            private final ImmutableMap.Builder<String, CraftableItem> craftableItemsBuilder;
            private final ImmutableMap.Builder<String, WearableItem> wearableItemsBuilder;
            private final ImmutableMap.Builder<String, Item> itemsByNameBuilder;
            private final ImmutableMap.Builder<String, Item> itemsByNameLowerCaseBuilder;

            private BuilderFiller(String id, Map<ItemLocation, ImmutableSet.Builder<Item>> itemsByItemLocationBuilder,
                                  ImmutableMap.Builder<Book, ImmutableSet.Builder<CraftableItem>> itemsByBookBuilder,
                                  Map<InventorySlot, ImmutableSet.Builder<WearableItem>> itemsByInventorySlotBuilder,
                                  Map<ItemType, ImmutableSet.Builder<WearableItem>> itemsByItemTypeBuilder,
                                  ImmutableMap.Builder<String, CraftableItem> craftableItemsBuilder,
                                  ImmutableMap.Builder<String, WearableItem> wearableItemsBuilder,
                                  ImmutableMap.Builder<String, Item> itemsByNameBuilder,
                                  ImmutableMap.Builder<String, Item> itemsByNameLowerCaseBuilder) {
                this.id = id;
                this.itemsByItemLocationBuilder = itemsByItemLocationBuilder;
                this.itemsByBookBuilder = itemsByBookBuilder;
                this.itemsByInventorySlotBuilder = itemsByInventorySlotBuilder;
                this.itemsByItemTypeBuilder = itemsByItemTypeBuilder;
                this.craftableItemsBuilder = craftableItemsBuilder;
                this.wearableItemsBuilder = wearableItemsBuilder;
                this.itemsByNameBuilder = itemsByNameBuilder;
                this.itemsByNameLowerCaseBuilder = itemsByNameLowerCaseBuilder;
            }

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
