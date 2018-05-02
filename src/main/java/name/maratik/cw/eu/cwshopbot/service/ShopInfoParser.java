package name.maratik.cw.eu.cwshopbot.service;

import name.maratik.cw.eu.cwshopbot.model.ShopInfo;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.MessageEntity;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Component
public class ShopInfoParser implements CWParser<ShopInfo> {

    private static final Comparator<MessageEntity> MESSAGE_ENTITY_OFFSET_COMPARATOR =
        Comparator.comparing(MessageEntity::getOffset);

    @Override
    public Optional<ShopInfo> parse(Message message) {
        if (!message.getText().startsWith("Welcome, to the ")) {
            return Optional.empty();
        }
        List<MessageEntity> messageEntities = message.getEntities();
        if (messageEntities == null) {
            return Optional.empty();
        }
        Iterator<MessageEntity> messageEntityIterator = messageEntities.stream()
            .sorted(MESSAGE_ENTITY_OFFSET_COMPARATOR)
            .limit(2)
            .iterator();
        Optional<String> shopName = extractBoldText(messageEntityIterator);
        if (!shopName.isPresent()) {
            return Optional.empty();
        }
        Optional<String> charName = extractBoldText(messageEntityIterator);
        if (!charName.isPresent()) {
            return Optional.empty();
        }
        Optional<String> shopCommand = messageEntities.stream()
            .max(MESSAGE_ENTITY_OFFSET_COMPARATOR)
            .map(MessageEntity::getText);
        return shopCommand
            .filter(s -> s.startsWith("/ws_"))
            .map(s -> ShopInfo.builder()
                    .setShopName(shopName.get())
                    .setCharName(charName.get())
                    .setShopCommand(s)
                    .build()
            );
    }

    private static Optional<String> extractBoldText(Iterator<MessageEntity> it) {
        if (!it.hasNext()) {
            return Optional.empty();
        }
        MessageEntity shopName = it.next();
        if (!"bold".equals(shopName.getType())) {
            return Optional.empty();
        }
        return Optional.ofNullable(shopName.getText());
    }
}
