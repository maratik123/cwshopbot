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
package name.maratik.cw.cwshopbot.application.dao;

import name.maratik.cw.cwshopbot.model.cwasset.Assets;
import name.maratik.cw.cwshopbot.model.cwasset.CraftableItem;
import name.maratik.cw.cwshopbot.model.cwasset.Craftbook;
import name.maratik.cw.cwshopbot.model.cwasset.InventorySlot;
import name.maratik.cw.cwshopbot.model.cwasset.Item;
import name.maratik.cw.cwshopbot.model.cwasset.ItemLocation;
import name.maratik.cw.cwshopbot.model.cwasset.ItemType;
import name.maratik.cw.cwshopbot.model.cwasset.WearableItem;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class AssetsDao {
    private final Logger logger = LogManager.getLogger(AssetsDao.class);

    private final AssetsDto assetsDto;

    public AssetsDao(Resource assets, TypeFactory typeFactory) throws IOException {
        logger.info("Loading assets");
        try (InputStream is = assets.getInputStream()) {
            assetsDto = new ObjectMapper(new YAMLFactory())
                .setTypeFactory(typeFactory)
                .registerModule(new Jdk8Module())
                .readValue(is, AssetsDto.class);
        }
        logger.info("Assets successfully loaded, assets parts size: {}, craftbooks: {}",
            () -> assetsDto.getAssetsPartMap().size(),
            () -> assetsDto.getCraftbook().size()
        );
    }

    @SuppressWarnings("WeakerAccess")
    public AssetsDto getAssetsDto() {
        return assetsDto;
    }

    public Assets createAssets() {
        logger.info("Decoding assets");
        Map<String, String> reverseCraftbookMap = assetsDto.getCraftbook().entrySet().stream()
            .flatMap(entry -> entry.getValue().getItems().stream()
                .map(id -> new AbstractMap.SimpleImmutableEntry<>(id, entry.getKey()))
            )
            .collect(toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));
        Assets result = Assets.builder()
            .setAllItemList(assetsDto.getAssetsPartMap().entrySet().stream()
                .flatMap(assetsPartEntry -> {
                    AssetsPartDto assetsPartDto = assetsPartEntry.getValue();
                    ItemLocation itemLocation = ItemLocation.findByCode(assetsPartEntry.getKey())
                        .orElseThrow(RuntimeException::new);
                    return assetsPartDto.getItems().entrySet().stream()
                        .map(itemEntry -> {
                            String id = itemEntry.getKey();
                            ResourceItem resourceItem = itemEntry.getValue();

                            Map<String, Integer> recipe = resourceItem.getRecipe();
                            if (recipe == null) {
                                return fillItemProps(Item.itemBuilder(), id, resourceItem, itemLocation, assetsPartDto)
                                    .build();
                            }
                            Craftbook craftbook = Craftbook.findByCode(reverseCraftbookMap.get(id))
                                .orElseThrow(RuntimeException::new);
                            ItemType itemType = resourceItem.getType();
                            if (itemType == null) {
                                return fillCraftableItemProps(CraftableItem.craftableItemBuilder(), id, resourceItem,
                                    itemLocation, assetsPartDto, craftbook, assetsDto, recipe)
                                    .build();
                            }
                            return fillWearableItemProps(WearableItem.wearableItemBuilder(), id, resourceItem, itemLocation,
                                assetsPartDto, craftbook, assetsDto, recipe, itemType)
                                .build();
                        });
                }).collect(toImmutableList())
            ).build();

        logger.info("Assets loaded. Assets size is: {}", () -> result.getAllItems().size());

        return result;
    }

    private static <T extends Item.AbstractItemBuilder<T, R>, R extends Item>
    T fillItemProps(T builder, String id, ResourceItem resourceItem, ItemLocation itemLocation,
                    AssetsPartDto assetsPartDto) {
        return builder
            .setId(id)
            .setName(resourceItem.getName())
            .setItemLocation(itemLocation)
            .setTradeable(Optional.ofNullable(resourceItem.getTradeable())
                .orElseGet(assetsPartDto::isTradeable)
            );
    }

    private static <T extends CraftableItem.AbstractCraftableItemBuilder<T, R>, R extends CraftableItem>
    T fillCraftableItemProps(T builder, String id, ResourceItem resourceItem, ItemLocation itemLocation,
                             AssetsPartDto assetsPartDto, Craftbook craftbook, AssetsDto assetsDto,
                             Map<String, Integer> recipe) {
        return fillItemProps(builder, id, resourceItem, itemLocation, assetsPartDto)
            .setCraftbook(craftbook)
            .setMana(Optional.ofNullable(resourceItem.getMana())
                .orElseGet(() -> assetsDto.getCraftbook().get(craftbook.getCode()).getMana())
            )
            .putAllRecipeItems(recipe);
    }

    private static <T extends WearableItem.AbstractWearableItemBuilder<T, R>, R extends WearableItem>
    T fillWearableItemProps(T builder, String id, ResourceItem resourceItem, ItemLocation itemLocation,
                            AssetsPartDto assetsPartDto, Craftbook craftbook, AssetsDto assetsDto,
                            Map<String, Integer> recipe, ItemType itemType) {
        return fillCraftableItemProps(builder, id, resourceItem, itemLocation, assetsPartDto, craftbook, assetsDto,
            recipe
        )
            .setAttack(resourceItem.getAtt())
            .setDefence(resourceItem.getDef())
            .setManaboost(resourceItem.getManaboost())
            .setInventorySlot(resourceItem.getWear())
            .setItemType(itemType);
    }

    public static class AssetsDto {
        @JsonProperty(required = true)
        private Map<String, CraftbookDto> craftbook;
        private final Map<String, AssetsPartDto> assetsPartMap = new HashMap<>();

        public Map<String, CraftbookDto> getCraftbook() {
            return craftbook;
        }

        public void setCraftbook(Map<String, CraftbookDto> craftbook) {
            this.craftbook = craftbook;
        }

        @SuppressWarnings("WeakerAccess")
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
        @JsonProperty(required = true)
        private boolean tradeable;
        private final Map<String, ResourceItem> items = new HashMap<>();

        @SuppressWarnings("WeakerAccess")
        public boolean isTradeable() {
            return tradeable;
        }

        public void setTradeable(boolean tradeable) {
            this.tradeable = tradeable;
        }

        @SuppressWarnings("WeakerAccess")
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
        @JsonProperty(required = true)
        private int mana;
        @JsonProperty(required = true)
        private Set<String> items;

        public int getMana() {
            return mana;
        }

        public void setMana(int mana) {
            this.mana = mana;
        }

        @SuppressWarnings("WeakerAccess")
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
        @JsonProperty(required = true)
        private String name;
        @JsonProperty
        private Boolean tradeable;
        @JsonProperty
        private Map<String, Integer> recipe;
        @JsonProperty
        private Integer mana;
        @JsonProperty
        private ItemType type;
        @JsonProperty
        private InventorySlot wear;
        @JsonProperty(defaultValue = "0")
        private int att;
        @JsonProperty(defaultValue = "0")
        private int def;
        @JsonProperty(defaultValue = "0")
        private int manaboost;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @SuppressWarnings("WeakerAccess")
        public Boolean getTradeable() {
            return tradeable;
        }

        public void setTradeable(Boolean tradeable) {
            this.tradeable = tradeable;
        }

        @SuppressWarnings("WeakerAccess")
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

        @SuppressWarnings("WeakerAccess")
        public InventorySlot getWear() {
            return wear;
        }

        public void setWear(InventorySlot wear) {
            this.wear = wear;
        }

        @SuppressWarnings("WeakerAccess")
        public int getAtt() {
            return att;
        }

        public void setAtt(int att) {
            this.att = att;
        }

        @SuppressWarnings("WeakerAccess")
        public int getDef() {
            return def;
        }

        public void setDef(int def) {
            this.def = def;
        }

        @SuppressWarnings("WeakerAccess")
        public int getManaboost() {
            return manaboost;
        }

        public void setManaboost(int manaboost) {
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
