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

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class CraftableItem extends Item {
    private final Map<String, Integer> recipe;
    private final int mana;
    private final Craftbook craftbook;

    public CraftableItem(String id, String name, ItemLocation itemLocation, boolean tradeable,
                         Map<String, Integer> recipe, int mana, Craftbook craftbook) {
        super(id, name, itemLocation, tradeable);
        this.recipe = Objects.requireNonNull(recipe);
        this.mana = mana;
        this.craftbook = Objects.requireNonNull(craftbook);
    }

    public Map<String, Integer> getRecipe() {
        return recipe;
    }

    public int getMana() {
        return mana;
    }

    public Craftbook getCraftbook() {
        return craftbook;
    }

    @Override
    void apply(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "CraftableItem{" +
            "recipe=" + recipe +
            ", mana=" + mana +
            ", craftbook=" + craftbook +
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
            return new CraftableItem(id, name, itemLocation, tradeable, recipeBuilder.build(), mana, craftbook);
        }
    }

    public abstract static class AbstractCraftableItemBuilder<T extends AbstractCraftableItemBuilder<T, R>, R>
        extends AbstractItemBuilder<T, R> {
        protected final ImmutableMap.Builder<String, Integer> recipeBuilder = ImmutableMap.builder();
        protected int mana;
        protected Craftbook craftbook;

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

        public T setCraftbook(Craftbook craftbook) {
            this.craftbook = craftbook;
            return getThis();
        }

    }
}
