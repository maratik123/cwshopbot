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
grammar ShopInfo;

@header {
package name.maratik.cw.eu.cwshopbot.parser.generated;
}

shopInfo: GREETING complexShopName DOT NEWLINE_CHAR
          charName WHITESPACE_CHARS currentMana SLASH_CHAR maxMana MANA_EMOJI THE profession FROM castle NEWLINE_CHAR
          NEWLINE_CHAR
          shopType IS shopState DOT NEWLINE_CHAR
          NEWLINE_CHAR
          shopLines
          NEWLINE_CHAR
          GOODBYE shopCommand ;
complexShopName: shopName WHITESPACE_CHARS shopType WHITESPACE_CHARS HASH_CHAR shopNumber ;
shopName: WORDS ;
charName: WORDS ;
currentMana: NUMBER ;
maxMana: NUMBER ;
profession: BLACKSMITH | ALCHEMIST ;
castle: MOONLIGHT | WOLFPACK | POTATO | SHARKTEETH | HIGHNEST | DEERHORN | DRAGONSCALE ;
shopType: WORD ;
shopState: OPEN | CLOSED ;
shopCommand: SHOPCOMMAND_PREFIX shopCode ;
shopCode: WORD ;
shopNumber: NUMBER ;
shopLines: shopLine+ ;
shopLine: itemName COMMA manaCost MANA_EMOJI price GOLD_EMOJI craftCommand NEWLINE_CHAR ;
itemName: WORDS ;
manaCost: NUMBER ;
price: NUMBER ;
craftCommand: shopCommand UNDERSCORE_CHAR itemCode ;
itemCode: WORD;

GREETING: 'Welcome, to the ' ;
DOT: WHITESPACE_CHARS? '.' WHITESPACE_CHARS? ;
NEWLINE_CHAR: '\n' ;
WHITESPACE_CHARS: ' '+ ;
SLASH_CHAR: '/' ;
UNDERSCORE_CHAR: '_' ;
MANA_EMOJI: WHITESPACE_CHARS? '💧' WHITESPACE_CHARS? ;
GOLD_EMOJI: WHITESPACE_CHARS? '💰' WHITESPACE_CHARS? ;
THE: WHITESPACE_CHARS? 'the' WHITESPACE_CHARS? ;
FROM: WHITESPACE_CHARS? 'from' WHITESPACE_CHARS? ;
IS: WHITESPACE_CHARS? 'is' WHITESPACE_CHARS? ;
GOODBYE: WORDS DOT ;
WORDS: WORD WHITESPACE_CHARS WORDS | WORD ;
WORD: LETTERNUM+ ;
fragment LETTERNUM: LETTER | DIGIT ;
fragment LETTER: [A-Z] | [a-z] ;
HASH_CHAR: '#' ;
fragment DIGIT: [0-9] ;
NUMBER: DIGIT+ ;
BLACKSMITH: 'Blacksmith' ;
ALCHEMIST: 'Alchemist' ;
MOONLIGHT: 'Moonlight' ;
WOLFPACK : 'Wolfpack' ;
POTATO: 'Potato' ;
SHARKTEETH: 'Sharkteeth' ;
HIGHNEST: 'Highnest' ;
DEERHORN: 'Deerhorn' ;
DRAGONSCALE: 'Dragonscale' ;
OPEN: 'Open' ;
CLOSED: 'Closed' ;
SHOPCOMMAND_PREFIX: SLASH_CHAR 'ws' UNDERSCORE_CHAR ;
COMMA: WHITESPACE_CHARS? ',' WHITESPACE_CHARS? ;
