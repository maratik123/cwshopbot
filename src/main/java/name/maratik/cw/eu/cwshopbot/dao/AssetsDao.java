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
package name.maratik.cw.eu.cwshopbot.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import name.maratik.cw.eu.cwshopbot.model.BodyPart;
import name.maratik.cw.eu.cwshopbot.model.ItemType;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Repository
public class AssetsDao {
    private final AssetsDto assetsDto;

    public AssetsDao(ResourceLoader resourceLoader) throws IOException {
        try (InputStream is = resourceLoader.getResource("classpath:assets/resources.yaml").getInputStream()) {
            assetsDto = new ObjectMapper(new YAMLFactory())
                .registerModule(new Jdk8Module()).readValue(is, AssetsDto.class);
        }
    }

    public AssetsDto getAssetsDto() {
        return assetsDto;
    }

    public class AssetsDto {
        private CraftbookMapDto craftbook;
        private Map<String, AssetsPartDto> assetsPartMap;

        public CraftbookMapDto getCraftbook() {
            return craftbook;
        }

        public void setCraftbook(CraftbookMapDto craftbook) {
            this.craftbook = craftbook;
        }

        public Map<String, AssetsPartDto> getAssetsPartMap() {
            return assetsPartMap;
        }

        public void setAssetsPartMap(Map<String, AssetsPartDto> assetsPartMap) {
            this.assetsPartMap = assetsPartMap;
        }

        @Override
        public String toString() {
            return "AssetsDto{" +
                "craftbook=" + craftbook +
                ", assetsPartMap=" + assetsPartMap +
                '}';
        }
    }

    public class AssetsPartDto {
        private boolean tradeable;
        private Map<String, ResourceItem> items;

        public boolean isTradeable() {
            return tradeable;
        }

        public void setTradeable(boolean tradeable) {
            this.tradeable = tradeable;
        }

        public Map<String, ResourceItem> getItems() {
            return items;
        }

        public void setItems(Map<String, ResourceItem> items) {
            this.items = items;
        }

        @Override
        public String toString() {
            return "AssetsPartDto{" +
                "tradeable=" + tradeable +
                ", items=" + items +
                '}';
        }
    }

    public class CraftbookMapDto {
        private Map<String, CraftbookDto> craftbookMap;

        public Map<String, CraftbookDto> getCraftbookMap() {
            return craftbookMap;
        }

        public void setCraftbookMap(Map<String, CraftbookDto> craftbookMap) {
            this.craftbookMap = craftbookMap;
        }

        @Override
        public String toString() {
            return "CraftbookMapDto{" +
                "craftbookMap=" + craftbookMap +
                '}';
        }
    }

    public class CraftbookDto {
        private int mana;
        private Set<String> items;

        public int getMana() {
            return mana;
        }

        public void setMana(int mana) {
            this.mana = mana;
        }

        public Set<String> getItems() {
            return items;
        }

        public void setItems(Set<String> items) {
            this.items = items;
        }

        @Override
        public String toString() {
            return "CraftbookDto{" +
                "mana=" + mana +
                ", items=" + items +
                '}';
        }
    }

    public class ResourceItem {
        private String name;
        private Boolean tradeable;
        private RecipeDto recipe;
        private Integer mana;
        private ItemType type;
        private BodyPart wear;
        private Integer att;
        private Integer def;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getTradeable() {
            return tradeable;
        }

        public void setTradeable(Boolean tradeable) {
            this.tradeable = tradeable;
        }

        public RecipeDto getRecipe() {
            return recipe;
        }

        public void setRecipe(RecipeDto recipe) {
            this.recipe = recipe;
        }

        public Integer getMana() {
            return mana;
        }

        public void setMana(Integer mana) {
            this.mana = mana;
        }

        public ItemType getType() {
            return type;
        }

        public void setType(ItemType type) {
            this.type = type;
        }

        public BodyPart getWear() {
            return wear;
        }

        public void setWear(BodyPart wear) {
            this.wear = wear;
        }

        public Integer getAtt() {
            return att;
        }

        public void setAtt(Integer att) {
            this.att = att;
        }

        public Integer getDef() {
            return def;
        }

        public void setDef(Integer def) {
            this.def = def;
        }

        @Override
        public String toString() {
            return "ResourceItem{" +
                "name='" + name + '\'' +
                ", tradeable=" + tradeable +
                ", recipe=" + recipe +
                ", mana=" + mana +
                ", type=" + type +
                ", wear=" + wear +
                ", att=" + att +
                ", def=" + def +
                '}';
        }
    }

    public class RecipeDto {
        private Map<String, Integer> recipeMap;

        public Map<String, Integer> getRecipeMap() {
            return recipeMap;
        }

        public void setRecipeMap(Map<String, Integer> recipeMap) {
            this.recipeMap = recipeMap;
        }

        @Override
        public String toString() {
            return "RecipeDto{" +
                "recipeMap=" + recipeMap +
                '}';
        }
    }
}
