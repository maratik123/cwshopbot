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

import lombok.Getter;
import lombok.ToString;

import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Getter
@ToString(callSuper = true)
public class WearableItem extends CraftableItem {
    private final ItemType itemType;
    private final InventorySlot inventorySlot;
    private final int attack;
    private final int defence;
    private final int manaboost;

    @SuppressWarnings("WeakerAccess")
    protected WearableItem(String id, String name, ItemLocation itemLocation, boolean tradeable, boolean ingredient,
                           Map<String, Integer> recipe, int mana, Craftbook craftbook, ItemType itemType,
                           InventorySlot inventorySlot, int attack, int defence, int manaboost) {
        super(id, name, itemLocation, tradeable, ingredient, recipe, mana, craftbook);
        this.itemType = Objects.requireNonNull(itemType, "itemType");
        this.inventorySlot = Objects.requireNonNull(inventorySlot, "inventorySlot");
        this.attack = attack;
        this.defence = defence;
        this.manaboost = manaboost;
    }

    @Override
    public <T> T apply(Visitor<? extends T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static WearableItemBuilder wearableItemBuilder() {
        return new WearableItemBuilder();
    }

    public static class WearableItemBuilder extends AbstractWearableItemBuilder<WearableItemBuilder, WearableItem> {

        @Override
        public WearableItemBuilder getThis() {
            return this;
        }

        @Override
        public WearableItem build() {
            return new WearableItem(id, name, itemLocation, tradeable, ingredient, recipeBuilder.build(), mana,
                craftbook, itemType, inventorySlot, attack, defence, manaboost);
        }
    }

    public abstract static class AbstractWearableItemBuilder<T extends AbstractWearableItemBuilder<T, R>, R extends WearableItem>
        extends AbstractCraftableItemBuilder<T, R> {
        @SuppressWarnings("WeakerAccess")
        protected ItemType itemType;
        @SuppressWarnings("WeakerAccess")
        protected InventorySlot inventorySlot;
        @SuppressWarnings("WeakerAccess")
        protected int attack;
        @SuppressWarnings("WeakerAccess")
        protected int defence;
        @SuppressWarnings("WeakerAccess")
        protected int manaboost;

        public T setItemType(ItemType itemType) {
            this.itemType = itemType;
            return getThis();
        }

        public T setInventorySlot(InventorySlot inventorySlot) {
            this.inventorySlot = inventorySlot;
            return getThis();
        }

        public T setAttack(int attack) {
            this.attack = clampToMax(attack);
            return getThis();
        }

        public T setDefence(int defence) {
            this.defence = clampToMax(defence);
            return getThis();
        }

        public T setManaboost(int manaboost) {
            this.manaboost = clampToMax(manaboost);
            return getThis();
        }

        private static int clampToMax(int value) {
            return value >= 0 ? value : Integer.MAX_VALUE;
        }
    }
}
