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

import name.maratik.cw.eu.cwshopbot.model.cwasset.Assets;
import name.maratik.cw.eu.cwshopbot.model.cwasset.CraftableItem;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Craftbook;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Item;
import name.maratik.cw.eu.cwshopbot.model.cwasset.WearableItem;
import name.maratik.cw.eu.cwshopbot.util.Localizable;

import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static name.maratik.cw.eu.cwshopbot.application.botcontroller.ShopController.A_PREFIX;
import static name.maratik.cw.eu.cwshopbot.application.botcontroller.ShopController.CRAFTBOOK_PREFIX;
import static name.maratik.cw.eu.cwshopbot.application.botcontroller.ShopController.RVIEW_PREFIX;
import static name.maratik.cw.eu.cwshopbot.application.botcontroller.ShopController.SHOP_SEARCH_PREFIX;
import static name.maratik.cw.eu.cwshopbot.application.botcontroller.ShopController.T_PREFIX;
import static name.maratik.cw.eu.cwshopbot.application.botcontroller.ShopController.VIEW_PREFIX;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.MANA;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.SHIELD;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.SWORDS;
import static name.maratik.cw.eu.cwshopbot.util.Utils.appendCommandLink;
import static name.maratik.cw.eu.cwshopbot.util.Utils.createCommandLink;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
public class ItemSearchService extends Localizable {
    private static final Comparator<Item> ITEM_NAME_COMPARATOR = Comparator.comparing(Item::getName);
    private static final Comparator<Map.Entry<Item, ?>> ITEM_NAME_IN_KEY_COMPARATOR =
        Comparator.comparing(Map.Entry::getKey, ITEM_NAME_COMPARATOR);
    private static final int LIST_LIMIT = 30;

    private final Assets assets;

    public ItemSearchService(Assets assets) {
        this.assets = assets;
    }

    public Optional<String> findByCodeThenByName(String search) {
        Optional<String> result = findByCode(search);
        if (result.isPresent()) {
            return result;
        }
        return findItemByName(search);
    }

    @SuppressWarnings("WeakerAccess")
    public List<Item> findItemByNameList(String name, boolean ignoreCase, boolean partialMatch) {
        final String searchName = ignoreCase
            ? name.toLowerCase()
            : name;
        if (!partialMatch) {
            final Map<String, Item> itemsByNameMap = ignoreCase
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
        List<Item> items = findItemByNameList(name, true, true);
        switch (items.size()) {
            case 0:
                return Optional.empty();
            case 1:
                return Optional.of(new ItemOutput(items.get(0)).getMessage());
            default:
                return Optional.of(new ListOutput(items).getMessage());
        }
    }

    public Optional<String> findByCode(String code) {
        String lowerCode = code.toLowerCase();
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
        Set<CraftableItem> items = Optional.ofNullable(assets.getCraftableItemsByRecipe().get(code))
            .orElseGet(Collections::emptySet);
        switch (items.size()) {
            case 0:
                return Optional.empty();
            case 1:
                return items.stream()
                    .findAny()
                    .map(RecipeOutput::new)
                    .map(SearchOutput::getMessage);
            default:
                Optional<Item> item = Optional.ofNullable(assets.getAllItems().get(code));
                return Optional.of(new ListRecipes(item, items).getMessage());
        }
    }

    private interface SearchOutput {
        String getMessage();
    }

    private class RecipeOutput implements SearchOutput {
        private final String message;

        private RecipeOutput(CraftableItem craftableItem) {
            Map<String, Integer> recipe = craftableItem.getRecipe();
            StringBuilder sb = new StringBuilder(t("iss.RECIPE.HEADER",
                craftableItem.getName(), createCommandLink(T_PREFIX, craftableItem), craftableItem.getMana()
            )).append("\n\n");
            recipe.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleImmutableEntry<>(
                    assets.getAllItems().get(entry.getKey()),
                    entry.getValue()
                ))
                .sorted(ITEM_NAME_IN_KEY_COMPARATOR)
                .forEach(entry -> {
                    Item item = entry.getKey();
                    sb.append(item.getName()).append(" (");
                    appendCommandLink(sb, A_PREFIX, item)
                        .append(") x ").append(entry.getValue()).append('\n');
                });
            message = sb.toString();
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    private class ItemOutput implements SearchOutput {
        private final String message;

        private ItemOutput(Item item) {
            message = item.apply(new MessageConstructor(new StringBuilder())).toString();
        }

        @Override
        public String getMessage() {
            return message;
        }

        private class MessageConstructor implements Item.Visitor<StringBuilder> {
            @SuppressWarnings("StringBufferField")
            private final StringBuilder sb;

            private MessageConstructor(StringBuilder sb) {
                this.sb = sb;
            }

            @Override
            public StringBuilder visit(Item item) {
                sb.append(t("iss.MESSAGE.ITEM.COMMON",
                    item.getId(), item.getName(), item.getItemLocation().getButtonText()
                )).append('\n');
                if (item.isTradeable()) {
                    sb.append(t("iss.MESSAGE.ITEM.TRADEABLE", createCommandLink(T_PREFIX, item))).append('\n');
                }
                if (assets.getCraftableItemsByRecipe().containsKey(item.getId())) {
                    sb.append(t("iss.MESSAGE.ITEM.RECIPES_WITH_ITEM", createCommandLink(RVIEW_PREFIX, item)))
                        .append('\n');
                }
                return sb;
            }

            @Override
            public StringBuilder visit(CraftableItem craftableItem) {
                Item.Visitor.super.visit(craftableItem);

                Craftbook craftbook = craftableItem.getCraftbook();
                if (craftbook.isVisible()) {
                    sb.append('\n')
                        .append(t("iss.MESSAGE.CRAFTABLE_ITEM",
                        createCommandLink(VIEW_PREFIX, craftableItem),
                        craftableItem.getMana(),
                        createCommandLink(CRAFTBOOK_PREFIX, craftbook.getCode()),
                        createCommandLink(SHOP_SEARCH_PREFIX, craftableItem)
                    )).append('\n');
                }
                return sb;
            }

            @Override
            public StringBuilder visit(WearableItem wearableItem) {
                Item.Visitor.super.visit(wearableItem).append('\n');

                boolean needNewLine = false;
                if (wearableItem.getAttack() > 0) {
                    sb.append(SWORDS + ": +").append(wearableItem.getAttack()).append(' ');
                    needNewLine = true;
                }
                if (wearableItem.getDefence() > 0) {
                    sb.append(SHIELD + ": +").append(wearableItem.getDefence()).append(' ');
                    needNewLine = true;
                }
                if (wearableItem.getManaboost() > 0) {
                    sb.append(MANA + ": +").append(wearableItem.getManaboost()).append(' ');
                    needNewLine = true;
                }
                if (needNewLine) {
                    sb.append('\n');
                }
                return sb.append(t("iss.MESSAGE.WEARABLE_ITEM",
                    wearableItem.getBodyPart().getCode(), wearableItem.getItemType().getCode()
                )).append('\n');
            }
        }
    }

    private static class ListOutput implements SearchOutput {

        private final String message;

        private ListOutput(List<? extends Item> items) {
            StringBuilder sb = new StringBuilder();
            items.forEach(item -> appendCommandLink(sb, T_PREFIX, item)
                .append(' ').append(item.getName()).append('\n')
            );
            message = sb.toString();
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    private class ListRecipes implements SearchOutput {
        private final String message;

        private ListRecipes(Optional<Item> optionalItem, Collection<CraftableItem> items) {
            StringBuilder sb = new StringBuilder();
            optionalItem.ifPresent(item ->
                sb.append(t("iss.RECIPE_LIST.HEADER",
                    item.getName(), createCommandLink(T_PREFIX, item)
                )).append("\n\n")
            );
            items.stream()
                .sorted(ITEM_NAME_COMPARATOR)
                .forEach(craftableItem -> {
                    appendCommandLink(sb, VIEW_PREFIX, craftableItem)
                        .append(' ').append(craftableItem.getName()).append(" (");
                    appendCommandLink(sb, T_PREFIX, craftableItem).append(")\n");
                });
            message = sb.toString();
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
