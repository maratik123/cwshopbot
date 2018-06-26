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
lexer grammar ShopEditLexer;

@header {
package name.maratik.cw.cwshopbot.parser.generated;
}

fragment LETTERDIGIT: LETTER | DIGIT;
fragment LETTER: LOWER_LETTER | UPPER_LETTER | [_()-];
fragment LOWER_LETTER: [a-zа-яё];
fragment UPPER_LETTER: [A-ZА-ЯЁ];
fragment DIGIT: ZERO | NONZERO_DIGIT;
fragment NONZERO_DIGIT: [1-9];
fragment ZERO: '0';
fragment WORD: LETTERDIGIT+;
fragment NUMBER_FRAGMENT: (NONZERO_DIGIT DIGIT* | ZERO);
fragment SHOP_EDIT_PREFIX: '/s_' NUMBER_FRAGMENT '_';
fragment MANA: '\u{1F4A7}';
fragment GOLD: '\u{1F4B0}';

CUSTOMIZE_YOUR: 'Customize your *';
WORDS: WORD (' ' WORD)*;
WS_HASH: ' #' -> pushMode(NUMBER_MODE);
DASH_USAGE_NL: ' - usage\n';
SHOP_HELP_COMMAND: SHOP_EDIT_PREFIX 'help';
OFFERS_WS_MUL: 'Offers *' -> pushMode(NUMBER_MODE);
SLASH: '/' -> pushMode(NUMBER_MODE);
MUL_NL: '*\n';
COMMA_WS: ', ' -> pushMode(NUMBER_MODE);
LINE_DELETE_COMMAND: SHOP_EDIT_PREFIX 'd_' NUMBER_FRAGMENT;
WS_LINK_WS: ' Link: ';
CANCEL_BELL: '\u{1F515}';
BELL: '\u{1F514}';
SHOP_COMMAND: '/ws_' WORD;
MANA_WS: MANA ' ' -> pushMode(NUMBER_MODE);
GOLD_WS: GOLD ' ';
NL: '\n';

mode NUMBER_MODE;

NUMBER: NUMBER_FRAGMENT -> popMode;
