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

import name.maratik.cw.eu.cwshopbot.model.character.Castle;
import name.maratik.cw.eu.cwshopbot.model.parser.ParsedHero;
import name.maratik.cw.eu.cwshopbot.parser.LoggingErrorListener;
import name.maratik.cw.eu.cwshopbot.parser.ParseException;
import name.maratik.cw.eu.cwshopbot.parser.generated.HeroLexer;
import name.maratik.cw.eu.cwshopbot.parser.generated.HeroParser;
import name.maratik.cw.eu.cwshopbot.parser.generated.HeroParserBaseListener;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Optional;

import static name.maratik.cw.eu.cwshopbot.parser.ParserUtils.catchParseErrors;
import static name.maratik.cw.eu.cwshopbot.util.Utils.reformatMessage;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Component
public class HeroParserService implements CWParser<ParsedHero> {
    private static final Logger logger = LogManager.getLogger(HeroParserService.class);

    @Override
    public Optional<ParsedHero> parse(Message message) {
        String formattedMessage = reformatMessage(message);
        int firstNewLine = formattedMessage.indexOf('\n');
        if (firstNewLine == -1) {
            return Optional.empty();
        }
        formattedMessage = formattedMessage.substring(0, firstNewLine + 1);
        CodePointCharStream messageCharStream = CharStreams.fromString(formattedMessage);
        HeroLexer lexer = new HeroLexer(messageCharStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new LoggingErrorListener());
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        HeroParser parser = new HeroParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());
        return catchParseErrors(() -> {
            ParsedHero.Builder builder = ParsedHero.builder();
            ParseTreeWalker.DEFAULT.walk(new HeroParserListenerImpl(builder), parser.hero());
            return Optional.of(builder.build());
        }, message);
    }

    private static class HeroParserListenerImpl extends HeroParserBaseListener {
        private final ParsedHero.Builder builder;

        private HeroParserListenerImpl(ParsedHero.Builder builder) {
            this.builder = builder;
        }

        @Override
        public void exitGuildAbbrev(HeroParser.GuildAbbrevContext ctx) {
            logger.trace("exitGuildAbbrev: {}", ctx::getText);
            builder.setGuildAbbrev(ctx.getText());
        }

        @Override
        public void exitCharName(HeroParser.CharNameContext ctx) {
            logger.trace("exitCharName: {}", ctx::getText);
            builder.setCharName(ctx.getText());
        }

        @Override
        public void exitOfCastleCastle(HeroParser.OfCastleCastleContext ctx) {
            logger.trace("exitOfCastleCastle: {}", ctx::getText);
            String text = ctx.getText();
            String[] textParts = text.split(" ");
            if (textParts.length != 3 ||
                !textParts[0].equals("of") ||
                !textParts[2].equals("Castle")) {
                throw new ParseException("Unknown castle: " + text);
            }
            String castle = textParts[1];
            builder.setCastle(Castle.findByCode(castle)
                .orElseThrow(() -> new ParseException("Unsupported castle: " + castle))
            );
        }
    }
}
