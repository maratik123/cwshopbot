package name.maratik.cw.eu.cwshopbot.model;

import org.telegram.telegrambots.api.objects.Message;

import java.time.Instant;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ForwardKey {
    private final Instant timestamp;
    private final String message;

    public ForwardKey(Instant timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    public static ForwardKey of(Instant timestamp, String message) {
        return new ForwardKey(timestamp, message);
    }

    public static ForwardKey of(Message message) {
        return of(Instant.ofEpochSecond(message.getForwardDate()), message.getText());
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ForwardKey that = (ForwardKey) o;

        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) {
            return false;
        }
        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        int result = timestamp != null ? timestamp.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
