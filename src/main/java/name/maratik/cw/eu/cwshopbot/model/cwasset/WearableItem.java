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

import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class WearableItem extends CraftableItem {
    private final ItemType itemType;
    private final BodyPart bodyPart;
    private final int attack;
    private final int defence;
    private final int manaboost;

    public WearableItem(String id, String name, ItemLocation itemLocation, boolean tradeable, Map<Item, Integer> recipe,
                        int mana, Craftbook craftbook, ItemType itemType, BodyPart bodyPart, int attack, int defence,
                        int manaboost) {
        super(id, name, itemLocation, tradeable, recipe, mana, craftbook);
        this.itemType = Objects.requireNonNull(itemType);
        this.bodyPart = Objects.requireNonNull(bodyPart);
        this.attack = attack;
        this.defence = defence;
        this.manaboost = manaboost;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public BodyPart getBodyPart() {
        return bodyPart;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefence() {
        return defence;
    }

    public int getManaboost() {
        return manaboost;
    }

    @Override
    void apply(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "WearableItem{" +
            "itemType=" + itemType +
            ", bodyPart=" + bodyPart +
            ", attack=" + attack +
            ", defence=" + defence +
            ", manaboost=" + manaboost +
            "} " + super.toString();
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
            return new WearableItem(id, name, itemLocation, tradeable, recipeBuilder.build(), mana, craftbook, itemType,
                bodyPart, attack, defence, manaboost);
        }
    }

    public abstract static class AbstractWearableItemBuilder<T extends AbstractWearableItemBuilder<T, R>, R>
        extends AbstractCraftableItemBuilder<T, R> {
        protected ItemType itemType;
        protected BodyPart bodyPart;
        protected int attack;
        protected int defence;
        protected int manaboost;

        public T setItemType(ItemType itemType) {
            this.itemType = itemType;
            return getThis();
        }

        public T setBodyPart(BodyPart bodyPart) {
            this.bodyPart = bodyPart;
            return getThis();
        }

        public T setAttack(int attack) {
            this.attack = attack;
            return getThis();
        }

        public T setDefence(int defence) {
            this.defence = defence;
            return getThis();
        }

        public T setManaboost(int manaboost) {
            this.manaboost = manaboost;
            return getThis();
        }
    }
}
