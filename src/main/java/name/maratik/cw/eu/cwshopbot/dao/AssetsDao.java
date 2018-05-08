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

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import name.maratik.cw.eu.cwshopbot.model.cwasset.BodyPart;
import name.maratik.cw.eu.cwshopbot.model.cwasset.ItemType;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class AssetsDao {
    private final AssetsDto assetsDto;

    public AssetsDao(Resource assets) throws IOException {
        try (InputStream is = assets.getInputStream()) {
            assetsDto = new ObjectMapper(new YAMLFactory())
                .registerModule(new Jdk8Module())
                .readValue(is, AssetsDto.class);
        }
    }

    public AssetsDto getAssetsDto() {
        return assetsDto;
    }

    public static class AssetsDto {
        private Map<String, CraftbookDto> craftbook;
        private final Map<String, AssetsPartDto> assetsPartMap = new HashMap<>();

        public Map<String, CraftbookDto> getCraftbook() {
            return craftbook;
        }

        public void setCraftbook(Map<String, CraftbookDto> craftbook) {
            this.craftbook = craftbook;
        }

        public Map<String, AssetsPartDto> getAssetsPartMap() {
            return assetsPartMap;
        }

        @JsonAnySetter
        public void putAssetsPart(String key, AssetsPartDto assetsPartDto) {
            assetsPartMap.put(key, assetsPartDto);
        }

        @Override
        public String toString() {
            return "AssetsDto{" +
                "craftbook=" + craftbook +
                ", assetsPartMap=" + assetsPartMap +
                '}';
        }
    }

    public static class AssetsPartDto {
        private boolean tradeable;
        private final Map<String, ResourceItem> items = new HashMap<>();

        public boolean isTradeable() {
            return tradeable;
        }

        public void setTradeable(boolean tradeable) {
            this.tradeable = tradeable;
        }

        public Map<String, ResourceItem> getItems() {
            return items;
        }

        @JsonAnySetter
        public void putItem(String key, ResourceItem resourceItem) {
            items.put(key, resourceItem);
        }

        @Override
        public String toString() {
            return "AssetsPartDto{" +
                "tradeable=" + tradeable +
                ", items=" + items +
                '}';
        }
    }

    public static class CraftbookDto {
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

    public static class ResourceItem {
        private String name;
        private Boolean tradeable;
        private Map<String, Integer> recipe;
        private Integer mana;
        private ItemType type;
        private BodyPart wear;
        private Integer att;
        private Integer def;
        private Integer manaboost;

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

        public Map<String, Integer> getRecipe() {
            return recipe;
        }

        public void setRecipe(Map<String, Integer> recipe) {
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

        public Integer getManaboost() {
            return manaboost;
        }

        public void setManaboost(Integer manaboost) {
            this.manaboost = manaboost;
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
                ", manaboost=" + manaboost +
                '}';
        }
    }
}
