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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Log4j2
public class AssetsDao {
    private final AssetsDto assetsDto;

    public AssetsDao(Resource assets, TypeFactory typeFactory) throws IOException {
        log.info("Loading assets");
        try (var is = assets.getInputStream()) {
            assetsDto = new ObjectMapper(new YAMLFactory())
                .setTypeFactory(typeFactory)
                .registerModules(
                    new AfterburnerModule(),
                    new Jdk8Module(),
                    new JavaTimeModule(),
                    new ParameterNamesModule()
                )
                .readValue(is, AssetsDto.class);
        }
        log.info("Assets successfully loaded, assets parts size: {}, craftbooks: {}",
            () -> assetsDto.getAssetsPartMap().size(),
            () -> assetsDto.getCraftbook().size()
        );
    }

    @SuppressWarnings("WeakerAccess")
    public AssetsDto getAssetsDto() {
        return assetsDto;
    }

    public Assets createAssets() {
        log.info("Decoding assets");
        Map<String, String> reverseCraftbookMap = assetsDto.getCraftbook().entrySet().stream()
            .flatMap(entry -> entry.getValue().getItems().stream()
                .map(id -> new SimpleImmutableEntry<>(id, entry.getKey()))
            )
            .collect(toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));
        var assets = Assets.builder()
            .setAllItemList(assetsDto.getAssetsPartMap().entrySet().stream()
                .flatMap(assetsPartEntry -> {
                    var assetsPartDto = assetsPartEntry.getValue();
                    var itemLocation = ItemLocation.findByCode(assetsPartEntry.getKey())
                        .orElseThrow(RuntimeException::new);
                    return assetsPartDto.getItems().entrySet().stream()
                        .map(itemEntry -> {
                            var id = itemEntry.getKey();
                            var resourceItem = itemEntry.getValue();

                            var recipe = resourceItem.getRecipe();
                            if (recipe == null) {
                                return fillItemProps(Item.itemBuilder(), id, resourceItem, itemLocation, assetsPartDto)
                                    .build();
                            }
                            var craftbook = Craftbook.findByCode(reverseCraftbookMap.get(id))
                                .orElseThrow(RuntimeException::new);
                            var itemType = resourceItem.getType();
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

        log.info("Assets loaded. Assets size is: {}", () -> assets.getAllItems().size());

        return assets;
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
            )
            .setIngredient(Optional.ofNullable(resourceItem.getIngredient())
                .orElseGet(assetsPartDto::isIngredient)
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

    @Data
    public static class AssetsDto {
        @JsonProperty(required = true)
        private Map<String, CraftbookDto> craftbook;
        private final Map<String, AssetsPartDto> assetsPartMap = new HashMap<>();

        @JsonAnySetter
        public void putAssetsPart(String key, AssetsPartDto assetsPartDto) {
            assetsPartMap.put(key, assetsPartDto);
        }
    }

    @Data
    public static class AssetsPartDto {
        @JsonProperty(required = true)
        private boolean tradeable;
        @JsonProperty(defaultValue = "false")
        private boolean ingredient;
        private final Map<String, ResourceItem> items = new HashMap<>();

        @JsonAnySetter
        public void putItem(String key, ResourceItem resourceItem) {
            items.put(key, resourceItem);
        }
    }

    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
    @Data
    public static class CraftbookDto {
        @JsonProperty(required = true)
        private int mana;
        @JsonProperty(required = true)
        private Set<String> items;

        public Set<String> getItems() {
            if (items == null) {
                items = new HashSet<>();
            }
            return items;
        }
    }

    @Data
    private static class ResourceItem {
        @JsonProperty(required = true)
        private String name;
        @JsonProperty
        private Boolean tradeable;
        @JsonProperty
        private Boolean ingredient;
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
    }
}
