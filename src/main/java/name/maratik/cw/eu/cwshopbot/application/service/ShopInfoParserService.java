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

import name.maratik.cw.eu.cwshopbot.model.ShopState;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Assets;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Castle;
import name.maratik.cw.eu.cwshopbot.model.cwasset.CraftableItem;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Item;
import name.maratik.cw.eu.cwshopbot.model.cwasset.Profession;
import name.maratik.cw.eu.cwshopbot.model.cwasset.WearableItem;
import name.maratik.cw.eu.cwshopbot.model.parser.ParsedShopInfo;
import name.maratik.cw.eu.cwshopbot.parser.ParseException;
import name.maratik.cw.eu.cwshopbot.parser.generated.ShopInfoBaseListener;
import name.maratik.cw.eu.cwshopbot.parser.generated.ShopInfoLexer;
import name.maratik.cw.eu.cwshopbot.parser.generated.ShopInfoParser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;

import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Component
public class ShopInfoParserService implements CWParser<ParsedShopInfo> {
    private static final Logger logger = LogManager.getLogger(ShopInfoParserService.class);

    private final Assets assets;
    private static final Item.Visitor CRAFTABLE_ITEM_VERIFIER = new Item.Visitor() {
        @Override
        public void visit(Item item) {
            logger.warn("Item {} is not craftable", item);
        }

        @Override
        public void visit(CraftableItem craftableItem) {
        }

        @Override
        public void visit(WearableItem wearableItem) {
        }
    };

    public ShopInfoParserService(Assets assets) {
        this.assets = assets;
    }

    @Override
    public Optional<ParsedShopInfo> parse(Message message) {
        CodePointCharStream messageCharStream = CharStreams.fromString(message.getText());
        ShopInfoLexer lexer = new ShopInfoLexer(messageCharStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ShopInfoParser parser = new ShopInfoParser(tokens);
        ParsedShopInfo.Builder builder = ParsedShopInfo.builder();
        try {
            ParseTreeWalker.DEFAULT.walk(new ShopInfoListenerImpl(builder), parser.shopInfo());
        } catch (Exception e) {
            logger.error("Failed to parse message {}", message, e);
            return Optional.empty();
        }
        return Optional.of(builder.build());
    }

    private class ShopInfoListenerImpl extends ShopInfoBaseListener {
        private final ParsedShopInfo.Builder builder;
        private boolean inComplexShopName = false;
        private boolean inCraftCommand = false;
        private String shopType = "";
        private String shopCode = "";
        private ParsedShopInfo.ShopLine.Builder shopLineBuilder;
        private Item item;
        private boolean isShopCodeSet = false;

        private ShopInfoListenerImpl(ParsedShopInfo.Builder builder) {
            this.builder = builder;
        }

        public ParsedShopInfo.Builder getBuilder() {
            return builder;
        }

        @Override
        public void enterComplexShopName(ShopInfoParser.ComplexShopNameContext ctx) {
            inComplexShopName = true;
        }

        @Override
        public void exitComplexShopName(ShopInfoParser.ComplexShopNameContext ctx) {
            inComplexShopName = false;
        }

        @Override
        public void exitShopName(ShopInfoParser.ShopNameContext ctx) {
            builder.setShopName(ctx.getText());
        }

        @Override
        public void exitCharName(ShopInfoParser.CharNameContext ctx) {
            builder.setCharName(ctx.getText());
        }

        @Override
        public void exitCurrentMana(ShopInfoParser.CurrentManaContext ctx) {
            String text = ctx.getText();
            try {
                builder.setCurrentMana(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported current mana value: " + text, e);
            }
        }

        @Override
        public void exitMaxMana(ShopInfoParser.MaxManaContext ctx) {
            String text = ctx.getText();
            try {
                builder.setMaxMana(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported max mana value: " + text, e);
            }
        }

        @Override
        public void exitProfession(ShopInfoParser.ProfessionContext ctx) {
            String text = ctx.getText();
            builder.setProfession(Profession.findByCode(text)
                .orElseThrow(() -> new ParseException("Unsupported profession: " + text))
            );
        }

        @Override
        public void exitCastle(ShopInfoParser.CastleContext ctx) {
            String text = ctx.getText();
            builder.setCastle(Castle.findByCode(text)
                .orElseThrow(() -> new ParseException("Unsupported castle: " + text))
            );
        }

        @Override
        public void exitShopType(ShopInfoParser.ShopTypeContext ctx) {
            String text = ctx.getText();
            if (inComplexShopName) {
                builder.setShopType(text);
                shopType = text;
            } else if (!shopType.equals(text)) {
                throw new ParseException("Shop name mismatch: expected = " + shopType + ", actual = " + text);
            }
        }

        @Override
        public void exitShopState(ShopInfoParser.ShopStateContext ctx) {
            String text = ctx.getText();
            builder.setShopState(ShopState.findByCode(text)
                .orElseThrow(() -> new ParseException("Unsupported shop state: " + text))
            );
        }

        @Override
        public void exitShopCode(ShopInfoParser.ShopCodeContext ctx) {
            String text = ctx.getText();
            if (inCraftCommand && !isShopCodeSet) {
                builder.setShopCode(text);
                shopCode = text;
                isShopCodeSet = true;
            } else if (!shopCode.equals(text)) {
                throw new ParseException("Shop code mismatch: expected = " + shopCode + ", actual = " + text);
            }
        }

        @Override
        public void enterCraftCommand(ShopInfoParser.CraftCommandContext ctx) {
            inCraftCommand = true;
        }

        @Override
        public void exitCraftCommand(ShopInfoParser.CraftCommandContext ctx) {
            inCraftCommand = false;
        }

        @Override
        public void exitShopNumber(ShopInfoParser.ShopNumberContext ctx) {
            String text = ctx.getText();
            try {
                builder.setShopNumber(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported shop number value: " + text, e);
            }
        }

        @Override
        public void enterShopLine(ShopInfoParser.ShopLineContext ctx) {
            shopLineBuilder = ParsedShopInfo.ShopLine.builder();
        }

        @Override
        public void exitShopLine(ShopInfoParser.ShopLineContext ctx) {
            builder.addShopLine(shopLineBuilder.build());
        }

        @Override
        public void exitItemName(ShopInfoParser.ItemNameContext ctx) {
            String text = ctx.getText();
            item = assets.getAllItems().get(text);
            if (item == null) {
                throw new ParseException("Unknown item name: " + text);
            }
            shopLineBuilder.setItem(item);
            item.apply(CRAFTABLE_ITEM_VERIFIER);
        }

        @Override
        public void exitManaCost(ShopInfoParser.ManaCostContext ctx) {
            String text = ctx.getText();
            int manaCost;
            try {
                manaCost = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported mana cost value: " + text, e);
            }
            shopLineBuilder.setMana(manaCost);
            item.apply(new ManaCostVerifier(manaCost));
        }

        @Override
        public void exitPrice(ShopInfoParser.PriceContext ctx) {
            String text = ctx.getText();
            try {
                shopLineBuilder.setPrice(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                throw new ParseException("Unsupported price value: " + text, e);
            }
        }

        @Override
        public void exitItemCode(ShopInfoParser.ItemCodeContext ctx) {
            String text = ctx.getText();
            if (!text.equals(item.getId())) {
                throw new ParseException("Item code mismatch: expected=" + item.getId() + ", actual=" + text);
            }
        }
    }

    private static class ManaCostVerifier implements Item.Visitor {
        private final int manaCost;

        private ManaCostVerifier(int manaCost) {
            this.manaCost = manaCost;
        }

        @Override
        public void visit(Item item) {
        }

        @Override
        public void visit(CraftableItem craftableItem) {
            if (craftableItem.getMana() != manaCost) {
                logger.warn("Mana cost is invalid for item {}: actual={}", craftableItem, manaCost);
            }
        }

        @Override
        public void visit(WearableItem wearableItem) {
            visit((CraftableItem) wearableItem);
        }
    }

}
