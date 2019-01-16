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

import name.maratik.cw.cwshopbot.model.ShopPublishStatus;
import name.maratik.cw.cwshopbot.model.cwasset.Item;
import name.maratik.cw.cwshopbot.model.parser.ParsedShopEdit;
import name.maratik.cw.cwshopbot.parser.LoggingErrorListener;
import name.maratik.cw.cwshopbot.parser.ParseException;
import name.maratik.cw.cwshopbot.parser.generated.ShopEditLexer;
import name.maratik.cw.cwshopbot.parser.generated.ShopEditParser;
import name.maratik.cw.cwshopbot.parser.generated.ShopEditParserBaseListener;

import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;

import static name.maratik.cw.cwshopbot.parser.ParserUtils.catchParseErrors;
import static name.maratik.cw.cwshopbot.util.Utils.reformatMessage;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
@Log4j2
public class ShopEditParserService implements CWParser<ParsedShopEdit> {

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
            ParsedShopEdit.ParsedShopEditBuilder builder = ParsedShopEdit.builder();
            ParseTreeWalker.DEFAULT.walk(new ShopEditParserListenerImpl(builder), parser.shopEdit());
            return Optional.of(builder.build());
        }, message);
    }

    private class ShopEditParserListenerImpl extends ShopEditParserBaseListener {
        private final ParsedShopEdit.ParsedShopEditBuilder builder;
        private ParsedShopEdit.ShopLine.ShopLineBuilder shopLineBuilder;

        private ShopEditParserListenerImpl(ParsedShopEdit.ParsedShopEditBuilder builder) {
            this.builder = builder;
        }

        @Override
        public void exitShopName(ShopEditParser.ShopNameContext ctx) {
            log.trace("exitShopName: {}", ctx::getText);
            builder.shopName(ctx.getText());
        }

        @Override
        public void exitShopNumber(ShopEditParser.ShopNumberContext ctx) {
            log.trace("exitShopNumber: {}", ctx::getText);
            String text = ctx.getText();
            try {
                builder.shopNumber(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported shop number value: " + text, e);
            }
        }

        @Override
        public void exitCurrentOffers(ShopEditParser.CurrentOffersContext ctx) {
            log.trace("exitCurrentOffers: {}", ctx::getText);
            String text = ctx.getText();
            try {
                builder.offersCount(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported current offers value: " + text, e);
            }
        }

        @Override
        public void exitMaxOffers(ShopEditParser.MaxOffersContext ctx) {
            log.trace("exitMaxOffers: {}", ctx::getText);
            String text = ctx.getText();
            try {
                builder.maxOffersCount(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported max offers value: " + text, e);
            }
        }

        @Override
        public void enterShopLine(ShopEditParser.ShopLineContext ctx) {
            log.trace("enterShopLine: {}", ctx::getText);
            shopLineBuilder = ParsedShopEdit.ShopLine.builder();
        }

        @Override
        public void exitShopLine(ShopEditParser.ShopLineContext ctx) {
            log.trace("exitShopLine: {}", ctx::getText);
            builder.shopLine(shopLineBuilder.build());
        }

        @Override
        public void exitItemName(ShopEditParser.ItemNameContext ctx) {
            log.trace("exitItemName: {}", ctx::getText);
            String text = ctx.getText();
            List<Item> items = itemSearchService.findItemByNameList(text, false, false);
            if (items.size() != 1) {
                throw new ParseException("Unknown item name: " + text);
            }
            shopLineBuilder.item(items.get(0));
        }

        @Override
        public void exitManaCost(ShopEditParser.ManaCostContext ctx) {
            log.trace("exitManaCost: {}", ctx::getText);
            String text = ctx.getText();
            try {
                shopLineBuilder.mana(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported mana cost value: " + text, e);
            }
        }

        @Override
        public void exitPrice(ShopEditParser.PriceContext ctx) {
            log.trace("exitPrice: {}", ctx::getText);
            String text = ctx.getText();
            try {
                shopLineBuilder.price(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported price value: " + text, e);
            }
        }

        @Override
        public void exitShopCommand(ShopEditParser.ShopCommandContext ctx) {
            log.trace("exitShopCommand: {}", ctx::getText);
            builder.shopCommand(ctx.getText());
        }

        @Override
        public void exitBellStatus(ShopEditParser.BellStatusContext ctx) {
            log.trace("exitBellStatus: {}", ctx::getText);
            String text = ctx.getText();
            builder.shopPublishStatus(ShopPublishStatus.findByValue(text)
                .orElseThrow(() -> new ParseException("Unsupported bell status: " + text))
            );
        }
    }
}
