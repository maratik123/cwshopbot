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

import name.maratik.cw.cwshopbot.model.Castle;
import name.maratik.cw.cwshopbot.model.Profession;
import name.maratik.cw.cwshopbot.model.ShopState;
import name.maratik.cw.cwshopbot.model.parser.ParsedShopInfo;
import name.maratik.cw.cwshopbot.parser.ParseException;
import name.maratik.cw.cwshopbot.util.Utils;
import name.maratik.cw.cwshopbot.model.Castle;
import name.maratik.cw.cwshopbot.model.Profession;
import name.maratik.cw.cwshopbot.model.ShopState;
import name.maratik.cw.cwshopbot.model.cwasset.Item;
import name.maratik.cw.cwshopbot.model.parser.ParsedShopInfo;
import name.maratik.cw.cwshopbot.parser.LoggingErrorListener;
import name.maratik.cw.cwshopbot.parser.ParseException;
import name.maratik.cw.cwshopbot.parser.generated.ShopInfoLexer;
import name.maratik.cw.cwshopbot.parser.generated.ShopInfoParser;
import name.maratik.cw.cwshopbot.parser.generated.ShopInfoParserBaseListener;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

import java.util.List;
import java.util.Optional;

import static name.maratik.cw.cwshopbot.parser.ParserUtils.catchParseErrors;
import static name.maratik.cw.cwshopbot.util.Utils.reformatMessage;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Component
public class ShopInfoParserService implements CWParser<ParsedShopInfo> {
    private static final Logger logger = LogManager.getLogger(ShopInfoParserService.class);

    private final ItemSearchService itemSearchService;

    public ShopInfoParserService(ItemSearchService itemSearchService) {
        this.itemSearchService = itemSearchService;
    }

    @Override
    public Optional<ParsedShopInfo> parse(Message message) {
        String formattedMessage = Utils.reformatMessage(message);
        CodePointCharStream messageCharStream = CharStreams.fromString(formattedMessage);
        ShopInfoLexer lexer = new ShopInfoLexer(messageCharStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new LoggingErrorListener());
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ShopInfoParser parser = new ShopInfoParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());
        return catchParseErrors(() -> {
            ParsedShopInfo.Builder builder = ParsedShopInfo.builder();
            ParseTreeWalker.DEFAULT.walk(new ShopInfoParserListener(builder), parser.shopInfo());
            return Optional.of(builder.build());
        }, message);
    }

    private class ShopInfoParserListener extends ShopInfoParserBaseListener {
        private final ParsedShopInfo.Builder builder;
        private ParsedShopInfo.ShopLine.Builder shopLineBuilder;

        private ShopInfoParserListener(ParsedShopInfo.Builder builder) {
            this.builder = builder;
        }

        @Override
        public void exitShopName(ShopInfoParser.ShopNameContext ctx) {
            logger.trace("exitShopName: {}", ctx::getText);
            builder.setShopName(ctx.getText());
        }

        @Override
        public void exitShopNumber(ShopInfoParser.ShopNumberContext ctx) {
            logger.trace("exitShopNumber: {}", ctx::getText);
            String text = ctx.getText();
            try {
                builder.setShopNumber(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported shop number value: " + text, e);
            }
        }

        @Override
        public void exitCharName(ShopInfoParser.CharNameContext ctx) {
            logger.trace("exitCharName: {}", ctx::getText);
            builder.setCharName(ctx.getText());
        }

        @Override
        public void exitCurrentMana(ShopInfoParser.CurrentManaContext ctx) {
            logger.trace("exitCurrentMana: {}", ctx::getText);
            String text = ctx.getText();
            try {
                builder.setCurrentMana(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported current mana value: " + text, e);
            }
        }

        @Override
        public void exitMaxMana(ShopInfoParser.MaxManaContext ctx) {
            logger.trace("exitMaxMana: {}", ctx::getText);
            String text = ctx.getText();
            try {
                builder.setMaxMana(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported max mana value: " + text, e);
            }
        }

        @Override
        public void exitProfessionFromCastle(ShopInfoParser.ProfessionFromCastleContext ctx) {
            logger.trace("exitProfessionFromCastle: {}", ctx::getText);
            String text = ctx.getText();
            String[] textParts = text.split(" ");
            if (textParts.length != 3 || !textParts[1].equals("from")) {
                throw new ParseException("Unknown profession from castle: " + text);
            }
            String profession = textParts[0];
            builder.setProfession(Profession.findByCode(profession)
                .orElseThrow(() -> new ParseException("Unsupported profession: " + profession))
            );
            String castle = textParts[2];
            builder.setCastle(Castle.findByCode(castle)
                .orElseThrow(() -> new ParseException("Unsupported castle: " + castle))
            );
        }

        @Override
        public void exitShopTypeIs(ShopInfoParser.ShopTypeIsContext ctx) {
            logger.trace("exitShopTypeIs: {}", ctx::getText);
            String text = ctx.getText();
            String[] textParts = text.split(" ");
            if (textParts.length != 2 || !textParts[1].equals("is")) {
                throw new ParseException("Unsupported shop type is: " + text);
            }
            builder.setShopType(textParts[0]);
        }

        @Override
        public void exitShopState(ShopInfoParser.ShopStateContext ctx) {
            logger.trace("exitShopState: {}", ctx::getText);
            String text = ctx.getText();
            builder.setShopState(ShopState.findByCode(text)
                .orElseThrow(() -> new ParseException("Unsupported shop state: " + text))
            );
        }

        @Override
        public void enterShopLine(ShopInfoParser.ShopLineContext ctx) {
            logger.trace("enterShopLine: {}", ctx::getText);
            shopLineBuilder = ParsedShopInfo.ShopLine.builder();
        }

        @Override
        public void exitItemName(ShopInfoParser.ItemNameContext ctx) {
            logger.trace("exitItemName: {}", ctx::getText);
            String text = ctx.getText();
            List<Item> items = itemSearchService.findItemByNameList(text, false, false);
            if (items.size() != 1) {
                throw new ParseException("Unknown item name: " + text);
            }
            shopLineBuilder.setItem(items.get(0));
        }

        @Override
        public void exitManaCost(ShopInfoParser.ManaCostContext ctx) {
            logger.trace("exitManaCost: {}", ctx::getText);
            String text = ctx.getText();
            try {
                shopLineBuilder.setMana(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported mana cost value: " + text, e);
            }
        }

        @Override
        public void exitPrice(ShopInfoParser.PriceContext ctx) {
            logger.trace("exitPrice: {}", ctx::getText);
            String text = ctx.getText();
            try {
                shopLineBuilder.setPrice(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported price value: " + text, e);
            }
        }

        @Override
        public void exitCraftCommand(ShopInfoParser.CraftCommandContext ctx) {
            logger.trace("exitCraftCommand: {}", ctx::getText);
            shopLineBuilder.setCraftCommand(ctx.getText());
        }

        @Override
        public void exitShopLine(ShopInfoParser.ShopLineContext ctx) {
            logger.trace("exitShopLine: {}", ctx::getText);
            builder.addShopLine(shopLineBuilder.build());
        }

        @Override
        public void exitShopCommand(ShopInfoParser.ShopCommandContext ctx) {
            logger.trace("exitShopCommand: {}", ctx::getText);
            builder.setShopCommand(ctx.getText());
        }
    }
}
