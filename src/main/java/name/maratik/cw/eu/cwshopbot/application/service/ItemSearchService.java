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
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.MANA;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.SHIELD;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.SWORDS;
import static name.maratik.cw.eu.cwshopbot.util.Utils.putCommandLink;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
public class ItemSearchService {
    private static final Comparator<Item> ITEM_NAME_COMPARATOR = Comparator.comparing(Item::getName);
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

    public List<Item> findItemByNameList(String name, boolean ignoreCase) {
        final String searchName;
        final Function<Item, String> nameExtractor;
        if (ignoreCase) {
            searchName = name.toLowerCase();
            nameExtractor = Item::getLowerName;
        } else {
            searchName = name;
            nameExtractor = Item::getName;
        }
        return assets.getAllItems().values().stream()
            .filter(item -> nameExtractor.apply(item).contains(searchName))
            .sorted(ITEM_NAME_COMPARATOR)
            .limit(LIST_LIMIT)
            .collect(toImmutableList());
    }

    public Optional<String> findItemByName(String name) {
        List<Item> items = findItemByNameList(name, true);
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

    private interface SearchOutput {
        String getMessage();
    }

    private class RecipeOutput implements SearchOutput {
        private final String message;

        private RecipeOutput(CraftableItem craftableItem) {
            Map<String, Integer> recipe = craftableItem.getRecipe();
            StringBuilder sb = new StringBuilder("Recipe for: *")
                .append(craftableItem.getName()).append("*\n")
                .append(MANA + " cost: ").append(craftableItem.getMana()).append("\n\n");
            recipe.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleImmutableEntry<>(
                    assets.getAllItems().get(entry.getKey()),
                    entry.getValue()
                ))
                .sorted(Comparator.comparing(entry -> entry.getKey().getName()))
                .forEach(entry -> {
                    Item item = entry.getKey();
                    sb.append(item.getName()).append(" (");
                    putCommandLink(sb, "/a_" + item.getId())
                        .append(") x ").append(entry.getValue()).append('\n');
                });
            message = sb.toString();
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    private static class ItemOutput implements SearchOutput {
        private final String message;

        private ItemOutput(Item item) {
            StringBuilder sb = new StringBuilder();
            item.apply(new MessageConstructor(sb));
            message = sb.toString();
        }

        @Override
        public String getMessage() {
            return message;
        }

        private static class MessageConstructor implements Item.Visitor {
            private final StringBuilder sb;

            private MessageConstructor(StringBuilder sb) {
                this.sb = sb;
            }

            @Override
            public void visit(Item item) {
                sb.append("Code: ").append(item.getId()).append('\n')
                    .append("Name: *").append(item.getName()).append("*\n")
                    .append("Located in: ").append(item.getItemLocation().getButtonText()).append('\n');
                if (item.isTradeable()) {
                    sb.append("Can be exchanged using ");
                    putCommandLink(sb, "/t_" + item.getId()).append(" command\n");
                }
            }

            @Override
            public void visit(CraftableItem craftableItem) {
                visit((Item) craftableItem);

                sb.append('\n')
                    .append("To view recipe click: ");
                putCommandLink(sb, "/view_" + craftableItem.getId()).append('\n')
                    .append(MANA + " cost: ").append(craftableItem.getMana()).append('\n')
                    .append("Craftbook: ").append(craftableItem.getCraftbook().getCode()).append('\n');
            }

            @Override
            public void visit(WearableItem wearableItem) {
                visit((CraftableItem) wearableItem);

                sb.append('\n');
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
                sb.append("Body part: ").append(wearableItem.getBodyPart().getCode()).append('\n')
                    .append("Class: ").append(wearableItem.getItemType().getCode()).append('\n');
            }
        }
    }

    private static class ListOutput implements SearchOutput {

        private final String message;

        private ListOutput(List<? extends Item> items) {
            StringBuilder sb = new StringBuilder();
            items.forEach(item -> putCommandLink(sb, "/t_" + item.getId())
                .append(' ').append(item.getName()).append('\n')
            );
            message = sb.toString();
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
