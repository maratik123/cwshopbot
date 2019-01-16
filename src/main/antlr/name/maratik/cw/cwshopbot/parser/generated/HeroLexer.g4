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
lexer grammar HeroLexer;

@header {
package name.maratik.cw.cwshopbot.parser.generated;
}

fragment MOON: '\u{1F311}';
fragment WOLF: '\u{1F43A}';
fragment POTATO: '\u{1F954}';
fragment SHARK: '\u{1F988}';
fragment EAGLE: '\u{1F985}';
fragment DEER: '\u{1F98C}';
fragment DRAGON: '\u{1F409}';
fragment LETTERDIGIT: LETTER | DIGIT;
fragment LETTER: LOWER_LETTER | UPPER_LETTER | [_()-];
fragment LOWER_LETTER: [a-zа-яё];
fragment UPPER_LETTER: [A-ZА-ЯЁ];
fragment DIGIT: ZERO | NONZERO_DIGIT;
fragment NONZERO_DIGIT: [1-9];
fragment ZERO: '0';
fragment WORD: LETTERDIGIT+;

CASTLE_SIGN: (MOON | WOLF | POTATO | SHARK | EAGLE | DEER | DRAGON);
WORDS: WORD (' ' WORD)*;
MUL_WS: '* ';
MUL: '*';
BRACKET_OPEN: '[';
BRACKET_CLOSE: ']';
NL: '\n';
