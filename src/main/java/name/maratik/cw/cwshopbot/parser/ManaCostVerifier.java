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
package name.maratik.cw.cwshopbot.parser;

import name.maratik.cw.cwshopbot.model.cwasset.CraftableItem;
import name.maratik.cw.cwshopbot.model.cwasset.Item;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Log4j2
@RequiredArgsConstructor
public class ManaCostVerifier implements Item.Visitor<Void> {
    private final int manaCost;

    @Override
    public Void visit(Item item) {
        log.warn("Trying to define mana cost '{}' for non-craftable item: {}", manaCost, item);
        return null;
    }

    @Override
    public Void visit(CraftableItem craftableItem) {
        if (craftableItem.getMana() != manaCost) {
            log.warn("Mana cost is invalid for item {}: actual={}", craftableItem, manaCost);
        }
        return null;
    }
}
