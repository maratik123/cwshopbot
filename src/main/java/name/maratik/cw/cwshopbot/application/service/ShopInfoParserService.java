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
import name.maratik.cw.cwshopbot.model.Profession;
import name.maratik.cw.cwshopbot.model.ShopState;
import name.maratik.cw.cwshopbot.model.cwasset.Item;
import name.maratik.cw.cwshopbot.model.parser.ParsedShopInfo;
import name.maratik.cw.cwshopbot.parser.LoggingErrorListener;
import name.maratik.cw.cwshopbot.parser.ParseException;
import name.maratik.cw.cwshopbot.parser.generated.ShopInfoLexer;
import name.maratik.cw.cwshopbot.parser.generated.ShopInfoParser;
import name.maratik.cw.cwshopbot.parser.generated.ShopInfoParserBaseListener;

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
public class ShopInfoParserService implements CWParser<ParsedShopInfo> {
    private final ItemSearchService itemSearchService;

    public ShopInfoParserService(ItemSearchService itemSearchService) {
        this.itemSearchService = itemSearchService;
    }

    @Override
    public Optional<ParsedShopInfo> parse(Message message) {
        String formattedMessage = reformatMessage(message);
        CodePointCharStream messageCharStream = CharStreams.fromString(formattedMessage);
        ShopInfoLexer lexer = new ShopInfoLexer(messageCharStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new LoggingErrorListener());
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ShopInfoParser parser = new ShopInfoParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());
        return catchParseErrors(() -> {
            ParsedShopInfo.ParsedShopInfoBuilder builder = ParsedShopInfo.builder();
            ParseTreeWalker.DEFAULT.walk(new ShopInfoParserListener(builder), parser.shopInfo());
            return Optional.of(builder.build());
        }, message);
    }

    private class ShopInfoParserListener extends ShopInfoParserBaseListener {
        private final ParsedShopInfo.ParsedShopInfoBuilder builder;
        private ParsedShopInfo.ShopLine.ShopLineBuilder shopLineBuilder;

        private ShopInfoParserListener(ParsedShopInfo.ParsedShopInfoBuilder builder) {
            this.builder = builder;
        }

        @Override
        public void exitShopName(ShopInfoParser.ShopNameContext ctx) {
            log.trace("exitShopName: {}", ctx::getText);
            builder.shopName(ctx.getText());
        }

        @Override
        public void exitShopNumber(ShopInfoParser.ShopNumberContext ctx) {
            log.trace("exitShopNumber: {}", ctx::getText);
            String text = ctx.getText();
            try {
                builder.shopNumber(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported shop number value: " + text, e);
            }
        }

        @Override
        public void exitCharName(ShopInfoParser.CharNameContext ctx) {
            log.trace("exitCharName: {}", ctx::getText);
            builder.charName(ctx.getText());
        }

        @Override
        public void exitCurrentMana(ShopInfoParser.CurrentManaContext ctx) {
            log.trace("exitCurrentMana: {}", ctx::getText);
            String text = ctx.getText();
            try {
                builder.currentMana(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported current mana value: " + text, e);
            }
        }

        @Override
        public void exitMaxMana(ShopInfoParser.MaxManaContext ctx) {
            log.trace("exitMaxMana: {}", ctx::getText);
            String text = ctx.getText();
            try {
                builder.maxMana(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported max mana value: " + text, e);
            }
        }

        @Override
        public void exitProfessionFromCastle(ShopInfoParser.ProfessionFromCastleContext ctx) {
            log.trace("exitProfessionFromCastle: {}", ctx::getText);
            String text = ctx.getText();
            String[] textParts = text.split(" ");
            if (textParts.length != 3 || !textParts[1].equals("from")) {
                throw new ParseException("Unknown profession from castle: " + text);
            }
            String profession = textParts[0];
            builder.profession(Profession.findByCode(profession)
                .orElseThrow(() -> new ParseException("Unsupported profession: " + profession))
            );
            String castle = textParts[2];
            builder.castle(Castle.findByCode(castle)
                .orElseThrow(() -> new ParseException("Unsupported castle: " + castle))
            );
        }

        @Override
        public void exitShopTypeIs(ShopInfoParser.ShopTypeIsContext ctx) {
            log.trace("exitShopTypeIs: {}", ctx::getText);
            String text = ctx.getText();
            String[] textParts = text.split(" ");
            if (textParts.length != 2 || !textParts[1].equals("is")) {
                throw new ParseException("Unsupported shop type is: " + text);
            }
            builder.shopType(textParts[0]);
        }

        @Override
        public void exitShopState(ShopInfoParser.ShopStateContext ctx) {
            log.trace("exitShopState: {}", ctx::getText);
            String text = ctx.getText();
            builder.shopState(ShopState.findByCode(text)
                .orElseThrow(() -> new ParseException("Unsupported shop state: " + text))
            );
        }

        @Override
        public void enterShopLine(ShopInfoParser.ShopLineContext ctx) {
            log.trace("enterShopLine: {}", ctx::getText);
            shopLineBuilder = ParsedShopInfo.ShopLine.builder();
        }

        @Override
        public void exitItemName(ShopInfoParser.ItemNameContext ctx) {
            log.trace("exitItemName: {}", ctx::getText);
            String text = ctx.getText();
            List<Item> items = itemSearchService.findItemByNameList(text, false, false);
            if (items.size() != 1) {
                throw new ParseException("Unknown item name: " + text);
            }
            shopLineBuilder.item(items.get(0));
        }

        @Override
        public void exitManaCost(ShopInfoParser.ManaCostContext ctx) {
            log.trace("exitManaCost: {}", ctx::getText);
            String text = ctx.getText();
            try {
                shopLineBuilder.mana(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported mana cost value: " + text, e);
            }
        }

        @Override
        public void exitPrice(ShopInfoParser.PriceContext ctx) {
            log.trace("exitPrice: {}", ctx::getText);
            String text = ctx.getText();
            try {
                shopLineBuilder.price(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported price value: " + text, e);
            }
        }

        @Override
        public void exitCraftCommand(ShopInfoParser.CraftCommandContext ctx) {
            log.trace("exitCraftCommand: {}", ctx::getText);
            shopLineBuilder.craftCommand(ctx.getText());
        }

        @Override
        public void exitShopLine(ShopInfoParser.ShopLineContext ctx) {
            log.trace("exitShopLine: {}", ctx::getText);
            builder.shopLine(shopLineBuilder.build());
        }

        @Override
        public void exitShopCommand(ShopInfoParser.ShopCommandContext ctx) {
            log.trace("exitShopCommand: {}", ctx::getText);
            builder.shopCommand(ctx.getText());
        }
    }
}
