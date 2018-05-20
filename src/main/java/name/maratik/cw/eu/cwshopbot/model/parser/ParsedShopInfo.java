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
package name.maratik.cw.eu.cwshopbot.model.parser;

import name.maratik.cw.eu.cwshopbot.model.ShopState;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Castle;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Item;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Profession;
import name.maratik.cw.eu.cwshopbot.parser.ParseException;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

import static name.maratik.cw.eu.cwshopbot.application.botcontroller.ShopController.SHOP_COMMAND_PREFIX;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ParsedShopInfo {
    private final String shopName;
    private final String charName;
    private final ShopState shopState;
    private final List<ShopLine> shopLines;
    private final String shopCode;
    private final int shopNumber;
    private final Castle castle;
    private final int currentMana;
    private final int maxMana;
    private final Profession profession;
    private final String shopType;
    private final String shopCommand;

    private ParsedShopInfo(String shopName, String charName, ShopState shopState, List<ShopLine> shopLines,
                           String shopCode, int shopNumber, Castle castle, int currentMana, int maxMana,
                           Profession profession, String shopType, String shopCommand) {
        this.shopName = Objects.requireNonNull(shopName);
        this.charName = Objects.requireNonNull(charName);
        this.shopState = Objects.requireNonNull(shopState);
        this.shopCommand = Objects.requireNonNull(shopCommand);
        if (!shopCommand.startsWith(SHOP_COMMAND_PREFIX)) {
            throw new ParseException("Shop command has unexpected format: " + shopCommand);
        }
        this.shopCode = Objects.requireNonNull(shopCode);
        if (!shopCommand.endsWith(shopCode)) {
            throw new ParseException("Shop command '" + shopCommand + "' does not contain shop code: " + shopCode);
        }
        this.shopLines = Objects.requireNonNull(shopLines);
        List<ShopLine> unknownLines = shopLines.stream()
            .filter(shopLine -> !shopLine.getCraftCommand().startsWith(shopCommand))
            .collect(toImmutableList());
        if (!unknownLines.isEmpty()) {
            StringBuilder sb = new StringBuilder("Next lines has unexpected command: ");
            unknownLines.forEach(shopLine -> sb
                .append("Item=")
                .append(shopLine.getItem().getId())
                .append(", command=")
                .append(shopLine.getCraftCommand())
                .append('\n'));
            throw new ParseException(sb.toString());
        }
        this.shopNumber = shopNumber;
        this.castle = Objects.requireNonNull(castle);
        this.currentMana = currentMana;
        this.maxMana = maxMana;
        this.profession = Objects.requireNonNull(profession);
        this.shopType = Objects.requireNonNull(shopType);
        if (!shopName.startsWith(shopType) && !shopName.endsWith(shopType)) {
            throw new ParseException("Shop name '" + shopName + "' does not contain shop type '" + shopType + '\'');
        }
    }

    public String getShopName() {
        return shopName;
    }

    public String getCharName() {
        return charName;
    }

    public ShopState getShopState() {
        return shopState;
    }

    public List<ShopLine> getShopLines() {
        return shopLines;
    }

    public String getShopCode() {
        return shopCode;
    }

    public int getShopNumber() {
        return shopNumber;
    }

    public Castle getCastle() {
        return castle;
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public Profession getProfession() {
        return profession;
    }

    public String getShopType() {
        return shopType;
    }

    public String getShopCommand() {
        return shopCommand;
    }

    @Override
    public String toString() {
        return "ParsedShopInfo{" +
            "shopName='" + shopName + '\'' +
            ", charName='" + charName + '\'' +
            ", shopState=" + shopState +
            ", shopLines=" + shopLines +
            ", shopCode='" + shopCode + '\'' +
            ", shopNumber=" + shopNumber +
            ", castle=" + castle +
            ", currentMana=" + currentMana +
            ", maxMana=" + maxMana +
            ", profession=" + profession +
            ", shopType='" + shopType + '\'' +
            ", shopCommand='" + shopCommand + '\'' +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String charName;
        private String shopName;
        private ShopState shopState;
        private int shopNumber;
        private final ImmutableList.Builder<ShopLine> shopLines = ImmutableList.builder();
        private Castle castle;
        private int currentMana;
        private int maxMana;
        private Profession profession;
        private String shopType;
        private String shopCommand;

        @SuppressWarnings("UnusedReturnValue")
        public Builder setCharName(String charName) {
            this.charName = charName;
            return this;
        }

        public Builder setShopName(String shopName) {
            this.shopName = shopName;
            return this;
        }

        public Builder setShopState(ShopState shopState) {
            this.shopState = shopState;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder addShopLine(ShopLine shopLine) {
            shopLines.add(Objects.requireNonNull(shopLine));
            return this;
        }

        public Builder setShopNumber(int shopNumber) {
            this.shopNumber = shopNumber;
            return this;
        }

        public Builder setCastle(Castle castle) {
            this.castle = castle;
            return this;
        }

        public Builder setCurrentMana(int currentMana) {
            this.currentMana = currentMana;
            return this;
        }

        public Builder setMaxMana(int maxMana) {
            this.maxMana = maxMana;
            return this;
        }

        public Builder setProfession(Profession profession) {
            this.profession = profession;
            return this;
        }

        public Builder setShopType(String shopType) {
            this.shopType = shopType;
            return this;
        }

        public Builder setShopCommand(String shopCommand) {
            this.shopCommand = shopCommand;
            return this;
        }

        public ParsedShopInfo build() {
            String shopCode = shopCommand.substring(SHOP_COMMAND_PREFIX.length());
            return new ParsedShopInfo(shopName, charName, shopState, shopLines.build(), shopCode, shopNumber, castle,
                currentMana, maxMana, profession, shopType, shopCommand);
        }
    }

    public static class ShopLine {
        private final Item item;
        private final int mana;
        private final int price;
        private final String craftCommand;

        private ShopLine(Item item, int mana, int price, String craftCommand) {
            this.item = Objects.requireNonNull(item);
            this.mana = mana;
            this.price = price;
            this.craftCommand = Objects.requireNonNull(craftCommand);
        }

        public Item getItem() {
            return item;
        }

        public int getMana() {
            return mana;
        }

        public int getPrice() {
            return price;
        }

        public String getCraftCommand() {
            return craftCommand;
        }

        @Override
        public String toString() {
            return "ShopLine{" +
                "item=" + item +
                ", mana=" + mana +
                ", price=" + price +
                ", craftCommand='" + craftCommand + '\'' +
                '}';
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Item item;
            private int mana;
            private int price;
            private String craftCommand;

            public Builder setItem(Item item) {
                this.item = item;
                return this;
            }

            public Builder setMana(int mana) {
                this.mana = mana;
                return this;
            }

            public Builder setPrice(int price) {
                this.price = price;
                return this;
            }

            public Builder setCraftCommand(String craftCommand) {
                this.craftCommand = craftCommand;
                return this;
            }

            public ShopLine build() {
                return new ShopLine(item, mana, price, craftCommand);
            }
        }
    }
}
