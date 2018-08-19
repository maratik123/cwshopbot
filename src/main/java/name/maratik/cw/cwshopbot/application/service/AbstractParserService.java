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
package name.maratik.cw.cwshopbot.application.service;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

import java.util.function.Supplier;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public abstract class AbstractParserService
    <T, Lex extends Lexer, Par extends Parser, Lsnr extends ParseTreeListener>
    implements CWParser<T> {
    private final Supplier<Lex> lexerSupplier;
    private final Supplier<Par> parserSupplier;
    private final Supplier<Lsnr> listenerSupplier;

    protected AbstractParserService(
        Supplier<Lex> lexerSupplier,
        Supplier<Par> parserSupplier,
        Supplier<Lsnr> listenerSupplier
    ) {

        this.lexerSupplier = lexerSupplier;
        this.parserSupplier = parserSupplier;
        this.listenerSupplier = listenerSupplier;
    }

    protected Lex createLexer() {
        return lexerSupplier.get();
    }

    protected Par createParser() {
        return parserSupplier.get();
    }

    protected Lsnr createListener() {
        return listenerSupplier.get();
    }
}
