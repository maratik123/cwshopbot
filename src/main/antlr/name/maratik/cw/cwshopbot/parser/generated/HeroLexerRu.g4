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
lexer grammar HeroLexerRu;

@header {
package name.maratik.cw.cwshopbot.parser.generated;
}
fragment TURTLE: '\u{1F422}';
fragment BAT: '\u{1F987}';
fragment MAPLE_LEAF: '\u{1F341}';
fragment SHAMROCK: '\u2618\uFE0F';
fragment BLACK_HEART: '\u{1F5A4}';
fragment ROSE: '\u{1F339}';
fragment AUBERGINE: '\u{1F346}';
fragment LETTERDIGIT: LETTER | DIGIT;
fragment LETTER: LOWER_LETTER | UPPER_LETTER | [_()-];
fragment LOWER_LETTER: [a-zа-яё];
fragment UPPER_LETTER: [A-ZА-ЯЁ];
fragment DIGIT: ZERO | NONZERO_DIGIT;
fragment NONZERO_DIGIT: [1-9];
fragment ZERO: '0';
fragment WORD: LETTERDIGIT+;

CASTLE_SIGN: (TURTLE | BAT | MAPLE_LEAF | SHAMROCK | BLACK_HEART | ROSE | AUBERGINE);
WORDS: WORD (' ' WORD)*;
MUL_WS: '* ';
MUL: '*';
BRACKET_OPEN: '[';
BRACKET_CLOSE: ']';
NL: '\n';
