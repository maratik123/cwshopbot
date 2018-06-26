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

import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class Item {
    private final String id;
    private final String name;
    private final ItemLocation itemLocation;
    private final boolean tradeable;
    private final String lowerName;

    protected Item(String id, String name, ItemLocation itemLocation, boolean tradeable) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.itemLocation = Objects.requireNonNull(itemLocation, "itemLocation");
        this.tradeable = tradeable;
        this.lowerName = name.toLowerCase();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemLocation getItemLocation() {
        return itemLocation;
    }

    public boolean isTradeable() {
        return tradeable;
    }

    public String getLowerName() {
        return lowerName;
    }

    public <T> T apply(Visitor<? extends T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Item)) {
            return false;
        }

        Item item = (Item) o;

        return getId().equals(item.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return "Item{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", itemLocation=" + itemLocation +
            ", tradeable=" + tradeable +
            '}';
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
            return new Item(id, name, itemLocation, tradeable);
        }
    }

    public abstract static class AbstractItemBuilder<T extends AbstractItemBuilder<T, R>, R extends Item> {
        @SuppressWarnings("WeakerAccess")
        protected String id;
        protected String name;
        @SuppressWarnings("WeakerAccess")
        protected ItemLocation itemLocation;
        @SuppressWarnings("WeakerAccess")
        protected boolean tradeable;

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
