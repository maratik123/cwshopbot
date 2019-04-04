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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Value
@Builder
public class YellowPage {
    private final String link;
    private final String name;
    private final String ownerName;
    private final CastleByEmoji ownerCastle;
    private final ProfessionByEmoji kind;
    private final int mana;
    private final List<Offer> offers;
    private final Map<Specialization, Integer> specialization;
    private final boolean active;
    private final boolean maintenanceEnabled;
    private final int maintenanceCost;
    private final int guildDiscount;
    private final int castleDiscount;

    private YellowPage(
        String link,
        String name,
        String ownerName,
        CastleByEmoji ownerCastle,
        ProfessionByEmoji kind,
        int mana,
        List<Offer> offers,
        Map<Specialization, Integer> specialization,
        boolean active,
        boolean maintenanceEnabled,
        int maintenanceCost,
        int guildDiscount,
        int castleDiscount
    ) {
        this.link = Objects.requireNonNull(link, "link");
        this.name = Objects.requireNonNull(name, "name");
        this.ownerName = Objects.requireNonNull(ownerName, "ownerName");
        this.ownerCastle = Objects.requireNonNull(ownerCastle, "ownerCastle");
        this.kind = Objects.requireNonNull(kind, "kind");
        this.mana = mana;
        this.offers = Optional.ofNullable(offers)
            .map(ImmutableList::copyOf)
            .orElseGet(ImmutableList::of);
        this.specialization = Optional.ofNullable(specialization)
            .map(Maps::immutableEnumMap)
            .orElseGet(ImmutableMap::of);
        this.active = active;
        this.maintenanceEnabled = maintenanceEnabled;
        this.maintenanceCost = maintenanceCost;
        this.guildDiscount = guildDiscount;
        this.castleDiscount = castleDiscount;
    }

    @JsonCreator
    public YellowPage(
        @JsonProperty("link") String link,
        @JsonProperty("name") String name,
        @JsonProperty("ownerName") String ownerName,
        @JsonProperty("ownerCastle") CastleByEmoji ownerCastle,
        @JsonProperty("kind") ProfessionByEmoji kind,
        @JsonProperty("mana") int mana,
        @JsonProperty("offers") List<Offer> offers,
        @JsonProperty("specialization") Map<Specialization, Integer> specialization,
        @JsonProperty("active") boolean active,
        @JsonProperty("maintenanceEnabled") Optional<Boolean> maintenanceEnabled,
        @JsonProperty("maintenanceEnabled") Optional<Integer> maintenanceCost,
        @JsonProperty("guildDiscount") OptionalInt guildDiscount,
        @JsonProperty("castleDiscount") OptionalInt castleDiscount
    ) {
        this(link, name, ownerName, ownerCastle, kind, mana, offers, specialization, active,
            maintenanceEnabled.orElse(false), maintenanceCost.orElse(0),
            guildDiscount.orElse(0), castleDiscount.orElse(0));
    }

    @Value
    @Builder
    public static class Offer {
        private final String item;
        private final int price;
        private final int mana;
        private final boolean active;

        public Offer(
            @JsonProperty("item") String item,
            @JsonProperty("price") int price,
            @JsonProperty("mana") int mana,
            @JsonProperty("active") boolean active
        ) {
            this.item = Objects.requireNonNull(item, "item");
            this.price = price;
            this.mana = mana;
            this.active = active;
        }
    }
}
