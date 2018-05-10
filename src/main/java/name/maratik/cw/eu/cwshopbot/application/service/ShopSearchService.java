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
package name.maratik.cw.eu.cwshopbot.application.service;

import name.maratik.cw.eu.cwshopbot.model.SearchDescriptor;
import name.maratik.cw.eu.cwshopbot.model.parser.ParsedShopInfo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
public class ShopSearchService {
    private static final Comparator<ParsedShopInfo> NOT_A_MOON_KING_DRIVEN_SORT_ORDER =
        Comparator.comparing((ParsedShopInfo parsedShopInfo) -> !parsedShopInfo.getShopCommand().equals("/ws_pj3q1"))
            .thenComparing(ParsedShopInfo::getShopCommand);

    public List<ParsedShopInfo> findShop(SearchDescriptor searchDescriptor) {
        List<ParsedShopInfo> stub = Collections.emptyList();
        return stub.stream()
            .sorted(NOT_A_MOON_KING_DRIVEN_SORT_ORDER)
            .collect(toList());
    }
}
