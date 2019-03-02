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
package name.maratik.cw.cwshopbot.application.service;

import name.maratik.cw.cwshopbot.model.cwasset.Assets;
import name.maratik.cw.cwshopbot.model.cwasset.CraftableItem;
import name.maratik.cw.cwshopbot.model.cwasset.Craftbook;
import name.maratik.cw.cwshopbot.model.cwasset.Item;
import name.maratik.cw.cwshopbot.model.cwasset.WearableItem;
import name.maratik.spring.telegram.util.Localizable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static name.maratik.cw.cwshopbot.application.botcontroller.ShopController.A_PREFIX;
import static name.maratik.cw.cwshopbot.application.botcontroller.ShopController.CRAFTBOOK_PREFIX;
import static name.maratik.cw.cwshopbot.application.botcontroller.ShopController.RVIEW_PREFIX;
import static name.maratik.cw.cwshopbot.application.botcontroller.ShopController.SHOP_SEARCH_PREFIX;
import static name.maratik.cw.cwshopbot.application.botcontroller.ShopController.T_PREFIX;
import static name.maratik.cw.cwshopbot.application.botcontroller.ShopController.VIEW_PREFIX;
import static name.maratik.cw.cwshopbot.util.Emoji.MANA;
import static name.maratik.cw.cwshopbot.util.Emoji.SHIELD;
import static name.maratik.cw.cwshopbot.util.Emoji.SWORDS;
import static name.maratik.cw.cwshopbot.util.Utils.appendCommandLink;
import static name.maratik.cw.cwshopbot.util.Utils.createCommandLink;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
public class ItemSearchService extends Localizable {
    private static final Comparator<Item> ITEM_NAME_COMPARATOR = Comparator.comparing(Item::getLowerName)
        .thenComparing(Item::getName)
        .thenComparing(Item::getId);
    private static final Comparator<Map.Entry<Item, ?>> ITEM_NAME_IN_KEY_COMPARATOR =
        Comparator.comparing(Map.Entry::getKey, ITEM_NAME_COMPARATOR);
    private static final int LIST_LIMIT = 30;

    private final Assets assets;

    public ItemSearchService(Assets assets) {
        this.assets = assets;
    }

    public Optional<String> findByCodeThenByName(String search) {
        var result = findByCode(search);
        if (result.isPresent()) {
            return result;
        }
        return findItemByName(search);
    }

    @SuppressWarnings("WeakerAccess")
    public List<Item> findItemByNameList(String name, boolean ignoreCase, boolean partialMatch) {
        final var searchName = ignoreCase
            ? name.toLowerCase()
            : name;
        if (!partialMatch) {
            final var itemsByNameMap = ignoreCase
                ? assets.getItemsByNameLowerCase()
                : assets.getItemsByName();
            return Optional.ofNullable(itemsByNameMap.get(searchName))
                .map(Collections::singletonList)
                .orElseGet(Collections::emptyList);
        }
        final Function<Item, String> nameExtractor = ignoreCase
            ? Item::getLowerName
            : Item::getName;
        return assets.getAllItems().values().stream()
            .filter(item -> nameExtractor.apply(item).contains(searchName))
            .sorted(ITEM_NAME_COMPARATOR)
            .limit(LIST_LIMIT)
            .collect(toImmutableList());
    }

    @SuppressWarnings("WeakerAccess")
    public Optional<String> findItemByName(String name) {
        return output(() -> {
            var items = findItemByNameList(name, true, true);
            switch (items.size()) {
                case 0:
                    return Optional.empty();
                case 1:
                    return items.stream()
                        .findAny()
                        .map(ItemOutput::new);
                default:
                    return Optional.of(items)
                        .map(ListOutput::new);
            }
        });
    }

    public Optional<String> findByCode(String code) {
        var lowerCode = code.toLowerCase();
        return Optional.ofNullable(assets.getAllItems().get(lowerCode))
            .map(ItemOutput::new)
            .map(SearchOutput::getMessage);
    }

    public Optional<String> findRecipeByCode(String code) {
        return Optional.ofNullable(assets.getCraftableItems().get(code))
            .map(RecipeOutput::new)
            .map(SearchOutput::getMessage);
    }

    public Optional<String> getCraftbook(String craftbookCode) {
        return Craftbook.findByCode(craftbookCode)
            .map(craftbook -> assets.getItemsByCraftbook().get(craftbook))
            .filter(craftableItems -> !craftableItems.isEmpty())
            .map(craftableItems -> craftableItems.stream()
                .sorted(ITEM_NAME_COMPARATOR)
                .collect(toImmutableList())
            )
            .map(ListOutput::new)
            .map(SearchOutput::getMessage);
    }

    public Optional<String> findRecipeByIncludedItem(String code) {
        return output(() -> {
            var items = Optional.ofNullable(assets.getCraftableItemsByRecipe().get(code))
                .orElseGet(Collections::emptySet);
            switch (items.size()) {
                case 0:
                    return Optional.empty();
                case 1:
                    return items.stream()
                        .findAny()
                        .map(RecipeOutput::new);
                default:
                    var item = Optional.ofNullable(assets.getAllItems().get(code));
                    return Optional.of(new ListRecipes(item, items));
            }
        });
    }

    private static Optional<String> output(Supplier<Optional<? extends SearchOutput>> searchOutputSupplier) {
        return searchOutputSupplier.get()
            .map(SearchOutput::getMessage);
    }

    private abstract static class SearchOutput {
        @Getter(lazy = true, onMethod_ = {@SuppressFBWarnings("JLM_JSR166_UTILCONCURRENT_MONITORENTER")})
        private final String message = evalMessage();

        protected abstract String evalMessage();
    }

    @RequiredArgsConstructor
    private class RecipeOutput extends SearchOutput {
        private final CraftableItem craftableItem;

        @Override
        protected String evalMessage() {
            var recipe = craftableItem.getRecipe();
            var sb = new StringBuilder(t("ItemSearchService.RECIPE.HEADER",
                craftableItem.getName(), createCommandLink(T_PREFIX, craftableItem), craftableItem.getMana()
            )).append('\n');
            recipe.entrySet().stream()
                .map(entry -> new SimpleImmutableEntry<>(
                    assets.getAllItems().get(entry.getKey()),
                    entry.getValue()
                ))
                .sorted(ITEM_NAME_IN_KEY_COMPARATOR)
                .forEach(entry -> {
                    var item = entry.getKey();
                    sb.append(item.getName()).append(" (");
                    appendCommandLink(sb, A_PREFIX, item)
                        .append(") x ").append(entry.getValue()).append('\n');
                });
            return sb.toString();
        }
    }

    @RequiredArgsConstructor
    private class ItemOutput extends SearchOutput {
        private final Item item;

        @Override
        protected String evalMessage() {
            return item.apply(new MessageConstructor(new StringBuilder())).toString();
        }

        @RequiredArgsConstructor
        private class MessageConstructor implements Item.Visitor<StringBuilder> {
            @SuppressWarnings("StringBufferField")
            private final StringBuilder sb;

            @Override
            public StringBuilder visit(Item item) {
                sb.append(t("ItemSearchService.MESSAGE.ITEM.COMMON",
                    item.getId(), item.getName(), t(item.getItemLocation())
                ));
                if (item.isTradeable()) {
                    sb.append(t("ItemSearchService.MESSAGE.ITEM.TRADEABLE", createCommandLink(T_PREFIX, item)));
                }
                if (assets.getCraftableItemsByRecipe().containsKey(item.getId())) {
                    sb.append(t("ItemSearchService.MESSAGE.ITEM.RECIPES_WITH_ITEM", createCommandLink(RVIEW_PREFIX, item)));
                }
                return sb;
            }

            @Override
            public StringBuilder visit(CraftableItem craftableItem) {
                Item.Visitor.super.visit(craftableItem);

                var craftbook = craftableItem.getCraftbook();
                if (craftbook.isVisible()) {
                    sb.append('\n')
                        .append(t("ItemSearchService.MESSAGE.CRAFTABLE_ITEM",
                        createCommandLink(VIEW_PREFIX, craftableItem),
                        craftableItem.getMana(),
                        createCommandLink(CRAFTBOOK_PREFIX, craftbook.getCode()),
                        createCommandLink(SHOP_SEARCH_PREFIX, craftableItem)
                    ));
                }
                return sb;
            }

            @Override
            public StringBuilder visit(WearableItem wearableItem) {
                Item.Visitor.super.visit(wearableItem).append('\n');

                var needNewLine = false;
                if (wearableItem.getAttack() > 0) {
                    sb.append(SWORDS + ": ");
                    appendPlusNumOrUnknown(wearableItem.getAttack()).append(' ');
                    needNewLine = true;
                }
                if (wearableItem.getDefence() > 0) {
                    sb.append(SHIELD + ": ");
                    appendPlusNumOrUnknown(wearableItem.getDefence()).append(' ');
                    needNewLine = true;
                }
                if (wearableItem.getManaboost() > 0) {
                    sb.append(MANA + ": ");
                    appendPlusNumOrUnknown(wearableItem.getManaboost()).append(' ');
                    needNewLine = true;
                }
                if (needNewLine) {
                    sb.append('\n');
                }
                return sb.append(t("ItemSearchService.MESSAGE.WEARABLE_ITEM",
                    t(wearableItem.getInventorySlot()), t(wearableItem.getItemType())
                ));
            }

            private StringBuilder appendPlusNumOrUnknown(int value) {
                if (value == Integer.MAX_VALUE) {
                    return sb.append(t("ItemSearchService.MESSAGE.STATS.UNKNOWN"));
                }
                return sb.append('+').append(value);
            }
        }
    }

    @RequiredArgsConstructor
    private static class ListOutput extends SearchOutput {
        private final List<? extends Item> items;

        @Override
        protected String evalMessage() {
            var sb = new StringBuilder();
            items.forEach(item -> appendCommandLink(sb, T_PREFIX, item)
                .append(' ').append(item.getName()).append('\n')
            );
            return sb.toString();
        }
    }

    @RequiredArgsConstructor
    private class ListRecipes extends SearchOutput {
        private final Optional<Item> optionalItem;
        private final Collection<CraftableItem> items;

        @Override
        protected String evalMessage() {
            var sb = new StringBuilder();
            optionalItem.ifPresent(item ->
                sb.append(t("ItemSearchService.RECIPE_LIST.HEADER",
                    item.getName(), createCommandLink(T_PREFIX, item)
                )).append('\n')
            );
            items.stream()
                .sorted(ITEM_NAME_COMPARATOR)
                .forEach(craftableItem -> {
                    appendCommandLink(sb, VIEW_PREFIX, craftableItem)
                        .append(' ').append(craftableItem.getName()).append(" (");
                    appendCommandLink(sb, T_PREFIX, craftableItem).append(")\n");
                });
            return sb.toString();
        }
    }
}
