package name.maratik.cw.cwshopbot.model.cwasset;

import name.maratik.cw.cwshopbot.util.EnumWithCode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public enum BookByCommand implements EnumWithCode {
    ALCHBOOK_1(Alchbook.ALCHBOOK_1),
    ALCHBOOK_2(Alchbook.ALCHBOOK_2),
    ALCHBOOK_3(Alchbook.ALCHBOOK_3),
    CRAFTBOOK_1(Craftbook.CRAFTBOOK_1),
    CRAFTBOOK_2(Craftbook.CRAFTBOOK_2),
    CRAFTBOOK_3(Craftbook.CRAFTBOOK_3);

    private final Book book;
    private final String code;
    private static final Map<String, BookByCommand> cache = Util.createCache(values());

    BookByCommand(Book book) {
        this.book = book;
        this.code = book.getCommandPrefix() + book.getCode();
    }

    @JsonValue
    @Override
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static Optional<BookByCommand> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }

    public Book getBook() {
        return book;
    }
}
