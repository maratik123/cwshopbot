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

import name.maratik.cw.cwshopbot.model.ShopPublishStatus;
import name.maratik.cw.cwshopbot.model.cwasset.Item;
import name.maratik.cw.cwshopbot.model.parser.ParsedShopEdit;
import name.maratik.cw.cwshopbot.parser.LoggingErrorListener;
import name.maratik.cw.cwshopbot.parser.ParseException;
import name.maratik.cw.cwshopbot.parser.generated.ShopEditLexer;
import name.maratik.cw.cwshopbot.parser.generated.ShopEditParser;
import name.maratik.cw.cwshopbot.parser.generated.ShopEditParserBaseListener;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;

import static name.maratik.cw.cwshopbot.parser.ParserUtils.catchParseErrors;
import static name.maratik.cw.cwshopbot.util.Utils.reformatMessage;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Component
public class ShopEditParserService implements CWParser<ParsedShopEdit> {
    private static final Logger logger = LogManager.getLogger(ShopEditParserService.class);

    private final ItemSearchService itemSearchService;

    public ShopEditParserService(ItemSearchService itemSearchService) {
        this.itemSearchService = itemSearchService;
    }

    @Override
    public Optional<ParsedShopEdit> parse(Message message) {
        String formattedMessage = reformatMessage(message);
        CodePointCharStream messageCharStream = CharStreams.fromString(formattedMessage);
        ShopEditLexer lexer = new ShopEditLexer(messageCharStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new LoggingErrorListener());
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ShopEditParser parser = new ShopEditParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());
        return catchParseErrors(() -> {
            ParsedShopEdit.Builder builder = ParsedShopEdit.builder();
            ParseTreeWalker.DEFAULT.walk(new ShopEditParserListenerImpl(builder), parser.shopEdit());
            return Optional.of(builder.build());
        }, message);
    }

    private class ShopEditParserListenerImpl extends ShopEditParserBaseListener {
        private final ParsedShopEdit.Builder builder;
        private ParsedShopEdit.ShopLine.Builder shopLineBuilder;

        private ShopEditParserListenerImpl(ParsedShopEdit.Builder builder) {
            this.builder = builder;
        }

        @Override
        public void exitShopName(ShopEditParser.ShopNameContext ctx) {
            logger.trace("exitShopName: {}", ctx::getText);
            builder.setShopName(ctx.getText());
        }

        @Override
        public void exitShopNumber(ShopEditParser.ShopNumberContext ctx) {
            logger.trace("exitShopNumber: {}", ctx::getText);
            String text = ctx.getText();
            try {
                builder.setShopNumber(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported shop number value: " + text, e);
            }
        }

        @Override
        public void exitCurrentOffers(ShopEditParser.CurrentOffersContext ctx) {
            logger.trace("exitCurrentOffers: {}", ctx::getText);
            String text = ctx.getText();
            try {
                builder.setOffersCount(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported current offers value: " + text, e);
            }
        }

        @Override
        public void exitMaxOffers(ShopEditParser.MaxOffersContext ctx) {
            logger.trace("exitMaxOffers: {}", ctx::getText);
            String text = ctx.getText();
            try {
                builder.setMaxOffersCount(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported max offers value: " + text, e);
            }
        }

        @Override
        public void enterShopLine(ShopEditParser.ShopLineContext ctx) {
            logger.trace("enterShopLine: {}", ctx::getText);
            shopLineBuilder = ParsedShopEdit.ShopLine.builder();
        }

        @Override
        public void exitShopLine(ShopEditParser.ShopLineContext ctx) {
            logger.trace("exitShopLine: {}", ctx::getText);
            builder.addShopLine(shopLineBuilder.build());
        }

        @Override
        public void exitItemName(ShopEditParser.ItemNameContext ctx) {
            logger.trace("exitItemName: {}", ctx::getText);
            String text = ctx.getText();
            List<Item> items = itemSearchService.findItemByNameList(text, false, false);
            if (items.size() != 1) {
                throw new ParseException("Unknown item name: " + text);
            }
            shopLineBuilder.setItem(items.get(0));
        }

        @Override
        public void exitManaCost(ShopEditParser.ManaCostContext ctx) {
            logger.trace("exitManaCost: {}", ctx::getText);
            String text = ctx.getText();
            try {
                shopLineBuilder.setMana(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported mana cost value: " + text, e);
            }
        }

        @Override
        public void exitPrice(ShopEditParser.PriceContext ctx) {
            logger.trace("exitPrice: {}", ctx::getText);
            String text = ctx.getText();
            try {
                shopLineBuilder.setPrice(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported price value: " + text, e);
            }
        }

        @Override
        public void exitShopCommand(ShopEditParser.ShopCommandContext ctx) {
            logger.trace("exitShopCommand: {}", ctx::getText);
            builder.setShopCommand(ctx.getText());
        }

        @Override
        public void exitBellStatus(ShopEditParser.BellStatusContext ctx) {
            logger.trace("exitBellStatus: {}", ctx::getText);
            String text = ctx.getText();
            builder.setShopPublishStatus(ShopPublishStatus.findByValue(text)
                .orElseThrow(() -> new ParseException("Unsupported bell status: " + text))
            );
        }
    }
}
