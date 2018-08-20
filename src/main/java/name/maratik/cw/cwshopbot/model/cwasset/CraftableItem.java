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

import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class CraftableItem extends Item {
    private final Map<String, Integer> recipe;
    private final int mana;
    private final Book book;

    @SuppressWarnings("WeakerAccess")
    protected CraftableItem(String id, String name, ItemLocation itemLocation, boolean tradeable,
                            Map<String, Integer> recipe, int mana, Book book) {
        super(id, name, itemLocation, tradeable);
        this.recipe = Objects.requireNonNull(recipe, "recipe");
        this.mana = mana;
        this.book = Objects.requireNonNull(book, "book");
    }

    public Map<String, Integer> getRecipe() {
        return recipe;
    }

    public int getMana() {
        return mana;
    }

    public Book getBook() {
        return book;
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

    @Override
    public String toString() {
        return "CraftableItem{" +
            "recipe=" + recipe +
            ", mana=" + mana +
            ", book=" + book +
            "} " + super.toString();
    }

    public static CraftableItemBuilder craftableItemBuilder() {
        return new CraftableItemBuilder();
    }

    public static class CraftableItemBuilder extends AbstractCraftableItemBuilder<CraftableItemBuilder, CraftableItem> {
        @Override
        public CraftableItemBuilder getThis() {
            return this;
        }

        @Override
        public CraftableItem build() {
            return new CraftableItem(id, name, itemLocation, tradeable, recipeBuilder.build(), mana, book);
        }
    }

    public abstract static class AbstractCraftableItemBuilder<T extends AbstractCraftableItemBuilder<T, R>, R extends CraftableItem>
        extends AbstractItemBuilder<T, R> {
        @SuppressWarnings("WeakerAccess")
        protected final ImmutableMap.Builder<String, Integer> recipeBuilder = ImmutableMap.builder();
        protected int mana;
        protected Book book;

        @SuppressWarnings("unused")
        public T putRecipeItem(String id, int quantity) {
            recipeBuilder.put(id, quantity);
            return getThis();
        }

        public T putAllRecipeItems(Map<String, Integer> recipe) {
            recipeBuilder.putAll(recipe);
            return getThis();
        }

        public T setMana(int mana) {
            this.mana = mana;
            return getThis();
        }

        public T setBook(Book book) {
            this.book = book;
            return getThis();
        }

    }
}
