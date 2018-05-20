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
parser grammar ShopInfoParser;

@header {
package name.maratik.cw.eu.cwshopbot.parser.generated;
}

options { tokenVocab=ShopInfoLexer; }

shopInfo:
 GREETINGS fullShopName DOT_NL
 MUL charName MUL_WS currentMana SLASH maxMana MANA_THE_WS professionFromCastle NL
 NL
 shopTypeIs WS_MUL shopState MUL DOT_NL
 NL
 shopLines
 NL
 WORDS DOT_WS shopCommand;

fullShopName: MUL shopName WS_HASH shopNumber MUL;
shopName: WORDS;
shopNumber: NUMBER;
charName: WORDS;
currentMana: NUMBER;
maxMana: NUMBER;
professionFromCastle: WORDS;
shopTypeIs: WORDS;
shopState: WORDS;
shopLines: shopLine+;
shopLine: itemName COMMA_WS manaCost MANA_WS price GOLD_WS craftCommand NL;
itemName: WORDS;
manaCost: NUMBER;
price: NUMBER;
craftCommand: CRAFT_COMMAND;
shopCommand: SHOP_COMMAND;
