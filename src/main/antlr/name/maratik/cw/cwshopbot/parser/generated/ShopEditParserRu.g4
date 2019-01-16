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
parser grammar ShopEditParserRu;

@header {
package name.maratik.cw.cwshopbot.parser.generated;
}

options { tokenVocab=ShopEditLexerRu; }

shopEdit:
 CUSTOMIZE_YOUR fullShopName MUL_NL
 SHOP_HELP_COMMAND DASH_USAGE_NL
 NL
 OFFERS_WS_MUL currentOffers SLASH maxOffers MUL_NL
 shopLines
 NL
 bellStatus WS_LINK_WS shopCommand;

fullShopName: shopName WS_HASH shopNumber;
shopName: WORDS;
shopNumber: NUMBER;
currentOffers: NUMBER;
maxOffers: NUMBER;
shopLines: shopLine+;
shopLine: itemName COMMA_WS manaCost MANA_WS price GOLD_WS LINE_DELETE_COMMAND NL;
itemName: WORDS;
manaCost: NUMBER;
price: NUMBER;
bellStatus: (BELL | CANCEL_BELL);
shopCommand: SHOP_COMMAND;
