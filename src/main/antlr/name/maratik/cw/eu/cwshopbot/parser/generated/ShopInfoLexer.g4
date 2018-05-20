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
lexer grammar ShopInfoLexer;

@header {
package name.maratik.cw.eu.cwshopbot.parser.generated;
}

fragment LETTERDIGIT: LETTER | DIGIT;
fragment LETTER: LOWER_LETTER | UPPER_LETTER | [_[\]()-];
fragment LOWER_LETTER: [a-z];
fragment UPPER_LETTER: [A-Z];
fragment DIGIT: ZERO | NONZERO_DIGIT;
fragment NONZERO_DIGIT: [1-9];
fragment ZERO: '0';
fragment WORD: LETTERDIGIT+;
fragment MANA: '\u{1F4A7}';
fragment GOLD: '\u{1F4B0}';

WORDS: WORD (' ' WORD)*;
WS_HASH: ' #' -> pushMode(NUMBER_MODE);
GREETINGS: 'Welcome, to the ';
DOT_NL: '.\n';
DOT_WS: '. ';
SLASH: '/' -> pushMode(NUMBER_MODE);
MANA_THE_WS: MANA 'the ';
NL: '\n';
MUL_WS: '* ' -> pushMode(NUMBER_MODE);
WS_MUL: ' *';
MUL: '*';
WS: ' ';
CRAFT_COMMAND: SHOP_COMMAND '_' WORD;
SHOP_COMMAND: '/ws_' WORD;
COMMA_WS: ', ' -> pushMode(NUMBER_MODE);
MANA_WS: (MANA ' ') -> pushMode(NUMBER_MODE);
GOLD_WS: GOLD ' ';

mode NUMBER_MODE;

NUMBER: (NONZERO_DIGIT DIGIT* | ZERO) -> popMode;
