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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Item {
    @EqualsAndHashCode.Include
    private final String id;
    private final String name;
    private final ItemLocation itemLocation;
    private final boolean tradeable;
    private final boolean ingredient;
    private final String lowerName;

    protected Item(String id, String name, ItemLocation itemLocation, boolean tradeable, boolean ingredient) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.itemLocation = Objects.requireNonNull(itemLocation, "itemLocation");
        this.tradeable = tradeable;
        this.ingredient = ingredient;
        this.lowerName = name.toLowerCase();
    }

    public <T> T apply(Visitor<? extends T> visitor) {
        return visitor.visit(this);
    }

    public static ItemBuilder itemBuilder() {
        return new ItemBuilder();
    }

    public static class ItemBuilder extends AbstractItemBuilder<ItemBuilder, Item> {
        @Override
        public ItemBuilder getThis() {
            return this;
        }

        @Override
        public Item build() {
            return new Item(id, name, itemLocation, tradeable, ingredient);
        }
    }

    public abstract static class AbstractItemBuilder<T extends AbstractItemBuilder<T, R>, R extends Item> {
        protected String id;
        protected String name;
        @SuppressWarnings("WeakerAccess")
        protected ItemLocation itemLocation;
        @SuppressWarnings("WeakerAccess")
        protected boolean tradeable;
        @SuppressWarnings("WeakerAccess")
        protected boolean ingredient;

        public abstract T getThis();
        public abstract R build();

        public T setId(String id) {
            this.id = id;
            return getThis();
        }

        public T setName(String name) {
            this.name = name;
            return getThis();
        }

        public T setItemLocation(ItemLocation itemLocation) {
            this.itemLocation = itemLocation;
            return getThis();
        }

        public T setTradeable(boolean tradeable) {
            this.tradeable = tradeable;
            return getThis();
        }

        public T setIngredient(boolean ingredient) {
            this.ingredient = ingredient;
            return getThis();
        }
    }

    public interface Visitor<T> {
        T visit(Item item);

        default T visit(CraftableItem craftableItem) {
            return visit((Item) craftableItem);
        }

        default T visit(WearableItem wearableItem) {
            return visit((CraftableItem) wearableItem);
        }
    }
}
