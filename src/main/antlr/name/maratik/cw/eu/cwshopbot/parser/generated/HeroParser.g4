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
parser grammar HeroParser;

@header {
package name.maratik.cw.eu.cwshopbot.parser.generated;
}

options { tokenVocab=HeroLexer; }

hero: castleSign MUL (guild?) charName MUL_WS ofCastleCastle NL;
castleSign: CASTLE_SIGN;
guild: BRACKET_OPEN guildAbbrev BRACKET_CLOSE;
guildAbbrev: WORDS;
charName: WORDS;
ofCastleCastle: WORDS;
