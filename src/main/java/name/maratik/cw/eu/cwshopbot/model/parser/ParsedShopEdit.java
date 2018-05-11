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

import java.util.List;
import java.util.Objects;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ParsedShopEdit {
    private final String shopName;
    private final String shopHelpCommand;
    private final int offerCount;
    private final int maxOfferCount;
    private final String shopCommand;
    private final List<ShopLine> shopLines;

    public ParsedShopEdit(String shopName, String shopHelpCommand, int offerCount, int maxOfferCount, String shopCommand, List<ShopLine> shopLines) {
        this.shopName = Objects.requireNonNull(shopName);
        this.shopHelpCommand = Objects.requireNonNull(shopHelpCommand);
        this.offerCount = offerCount;
        this.maxOfferCount = maxOfferCount;
        this.shopCommand = Objects.requireNonNull(shopCommand);
        this.shopLines = Objects.requireNonNull(shopLines);
    }

    public String getShopName() {
        return shopName;
    }

    public String getShopHelpCommand() {
        return shopHelpCommand;
    }

    public int getOfferCount() {
        return offerCount;
    }

    public int getMaxOfferCount() {
        return maxOfferCount;
    }

    public String getShopCommand() {
        return shopCommand;
    }

    public List<ShopLine> getShopLines() {
        return shopLines;
    }

    @Override
    public String toString() {
        return "ParsedShopEdit{" +
            "shopName='" + shopName + '\'' +
            ", shopHelpCommand='" + shopHelpCommand + '\'' +
            ", offerCount=" + offerCount +
            ", maxOfferCount=" + maxOfferCount +
            ", shopCommand='" + shopCommand + '\'' +
            ", shopLines=" + shopLines +
            '}';
    }

    private static class ShopLine {
    }
}
