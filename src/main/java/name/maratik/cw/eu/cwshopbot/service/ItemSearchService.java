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
package name.maratik.cw.eu.cwshopbot.service;

import name.maratik.cw.eu.cwshopbot.model.cwasset.Assets;
import name.maratik.cw.eu.cwshopbot.model.cwasset.CraftableItem;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Item;
import name.maratik.cw.eu.cwshopbot.model.cwasset.WearableItem;
import org.springframework.stereotype.Service;

import java.util.Comparator;

import static name.maratik.cw.eu.cwshopbot.util.Emoji.MANA;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.SHIELD;
import static name.maratik.cw.eu.cwshopbot.util.Emoji.SWORDS;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
public class ItemSearchService {
    private static final Comparator<Item> ITEM_NAME_COMPARATOR = Comparator.comparing(Item::getName);
    private static final int LIST_LIMIT = 20;

    private final Assets assets;

    public ItemSearchService(Assets assets) {
        this.assets = assets;
    }

    public String findByCode(String code) {
        Item item = assets.getAllItems().get(code);
        if (item == null) {
            return "404 Not found";
        }
        return new ItemOutput(item).getMessage();
    }

    private interface SearchOutput {
        String getMessage();
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
                sb.append("Code: ").append(item.getId()).append('\n');
                sb.append("Name: ").append(item.getName()).append('\n');
                sb.append("Located in: ").append(item.getItemLocation().getButtonText()).append('\n');
                if (item.isTradeable()) {
                    sb.append("Can be exchanged using /t_").append(item.getId()).append(" command\n");
                }
            }

            @Override
            public void visit(CraftableItem craftableItem) {
                visit((Item) craftableItem);

                sb.append('\n');
                sb.append("To view recipe click: /view_").append(craftableItem.getId()).append('\n');
                sb.append(MANA + " cost: ").append(craftableItem.getMana()).append('\n');
                sb.append("Craftbook: ").append(craftableItem.getCraftbook().getCode()).append('\n');
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
                sb.append("Body part: ").append(wearableItem.getBodyPart().getCode()).append('\n');
                sb.append("Class: ").append(wearableItem.getItemType().getCode()).append('\n');
            }
        }
    }

    private static class ListOutput implements SearchOutput {

        @Override
        public String getMessage() {
            return "";
        }
    }
}
