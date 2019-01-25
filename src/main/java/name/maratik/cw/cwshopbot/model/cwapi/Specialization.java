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
package name.maratik.cw.cwshopbot.model.cwapi;

import name.maratik.cw.cwshopbot.model.cwasset.InventorySlot;
import name.maratik.cw.cwshopbot.util.EnumWithCode;
import name.maratik.spring.telegram.util.LocalizableValue;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Maps.toImmutableEnumMap;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@RequiredArgsConstructor
public enum Specialization implements EnumWithCode, LocalizableValue {
    BOOTS("boots", InventorySlot.LEGS),
    HELMET("helmet", InventorySlot.HEAD),
    SHIELD("shield", InventorySlot.SECONDARY_HAND),
    ARMOR("armor", InventorySlot.BODY),
    WEAPON("weapon", InventorySlot.PRIMARY_HAND),
    COAT("coat", InventorySlot.CLOAK),
    GLOVES("gloves", InventorySlot.HANDS);

    @Getter(onMethod_ = @JsonValue)
    private final String code;

    @Getter
    private final InventorySlot inventorySlot;

    private static final Map<String, Specialization> cache = Util.createCache(values());
    private static final Map<InventorySlot, Specialization> inventorySlotCache = Arrays.stream(values())
        .collect(toImmutableEnumMap(
            Specialization::getInventorySlot,
            t -> t
        ));

    public static Optional<Specialization> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }

    public static Specialization findByInventorySlot(InventorySlot inventorySlot) {
        return inventorySlotCache.get(inventorySlot);
    }

    @SuppressWarnings("Convert2Lambda")
    public static final Converter<String, Specialization> CONVERTER = new Converter<String, Specialization>() {
        @Override
        public Specialization convert(@NonNull String source) {
            return findByCode(source).orElseThrow(() -> new IllegalArgumentException("Unknown specialization: " + source));
        }
    };

    @Override
    public String getTranslationTag() {
        return inventorySlot.getTranslationTag();
    }
}
