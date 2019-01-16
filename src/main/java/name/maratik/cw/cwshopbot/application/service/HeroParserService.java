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
package name.maratik.cw.cwshopbot.application.service;

import name.maratik.cw.cwshopbot.model.Castle;
import name.maratik.cw.cwshopbot.model.parser.ParsedHero;
import name.maratik.cw.cwshopbot.parser.LoggingErrorListener;
import name.maratik.cw.cwshopbot.parser.ParseException;
import name.maratik.cw.cwshopbot.parser.generated.HeroLexer;
import name.maratik.cw.cwshopbot.parser.generated.HeroParser;
import name.maratik.cw.cwshopbot.parser.generated.HeroParserBaseListener;

import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

import static name.maratik.cw.cwshopbot.parser.ParserUtils.catchParseErrors;
import static name.maratik.cw.cwshopbot.util.Utils.reformatMessage;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
@Log4j2
public class HeroParserService implements CWParser<ParsedHero> {

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
            ParsedHero.ParsedHeroBuilder builder = ParsedHero.builder();
            ParseTreeWalker.DEFAULT.walk(new HeroParserListenerImpl(builder), parser.hero());
            return Optional.of(builder.build());
        }, message);
    }

    private static class HeroParserListenerImpl extends HeroParserBaseListener {
        private final ParsedHero.ParsedHeroBuilder builder;

        private HeroParserListenerImpl(ParsedHero.ParsedHeroBuilder builder) {
            this.builder = builder;
        }

        @Override
        public void exitGuildAbbrev(HeroParser.GuildAbbrevContext ctx) {
            log.trace("exitGuildAbbrev: {}", ctx::getText);
            builder.guildAbbrev(Optional.ofNullable(ctx.getText()));
        }

        @Override
        public void exitCharName(HeroParser.CharNameContext ctx) {
            log.trace("exitCharName: {}", ctx::getText);
            builder.charName(ctx.getText());
        }

        @Override
        public void exitOfCastleCastle(HeroParser.OfCastleCastleContext ctx) {
            log.trace("exitOfCastleCastle: {}", ctx::getText);
            String text = ctx.getText();
            String[] textParts = text.split(" ");
            if (textParts.length != 3 ||
                !textParts[0].equals("of") ||
                !textParts[2].equals("Castle")) {
                throw new ParseException("Unknown castle: " + text);
            }
            String castle = textParts[1];
            builder.castle(Castle.findByCode(castle)
                .orElseThrow(() -> new ParseException("Unsupported castle: " + castle))
            );
        }
    }
}
