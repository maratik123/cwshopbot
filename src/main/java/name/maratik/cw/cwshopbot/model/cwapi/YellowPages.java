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
package name.maratik.cw.cwshopbot.model.cwapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class YellowPages {
    private final String link;
    private final String name;
    private final String ownerName;
    private final CastleByEmoji ownerCastle;
    private final ProfessionByEmoji kind;
    private final int mana;
    private final List<Offer> offers;

    public YellowPages(
        @JsonProperty("link") String link,
        @JsonProperty("name") String name,
        @JsonProperty("ownerName") String ownerName,
        @JsonProperty("ownerCastle") CastleByEmoji ownerCastle,
        @JsonProperty("kind") ProfessionByEmoji kind,
        @JsonProperty("mana") int mana,
        @JsonProperty("offers") List<Offer> offers
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
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public CastleByEmoji getOwnerCastle() {
        return ownerCastle;
    }

    public ProfessionByEmoji getKind() {
        return kind;
    }

    public int getMana() {
        return mana;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    @Override
    public String toString() {
        return "YellowPages{" +
            "link='" + link + '\'' +
            ", name='" + name + '\'' +
            ", ownerName='" + ownerName + '\'' +
            ", ownerCastle=" + ownerCastle +
            ", kind=" + kind +
            ", mana=" + mana +
            ", offers=" + offers +
            '}';
    }

    public static class Offer {
        private final String item;
        private final int price;
        private final int mana;

        public Offer(
            @JsonProperty("item") String item,
            @JsonProperty("price") int price,
            @JsonProperty("mana") int mana
        ) {
            this.item = Objects.requireNonNull(item, "item");
            this.price = price;
            this.mana = mana;
        }

        public String getItem() {
            return item;
        }

        public int getPrice() {
            return price;
        }

        public int getMana() {
            return mana;
        }

        @Override
        public String toString() {
            return "Offer{" +
                "item='" + item + '\'' +
                ", price=" + price +
                ", mana=" + mana +
                '}';
        }
    }
}
