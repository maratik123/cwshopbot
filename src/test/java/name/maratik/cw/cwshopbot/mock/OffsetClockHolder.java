package name.maratik.cw.cwshopbot.mock;

import name.maratik.cw.cwshopbot.application.config.ClockHolder;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class OffsetClockHolder extends ClockHolder {
    private final AtomicReference<Clock> clockRef = new AtomicReference<>(Clock.systemUTC());

    public void reset() {
        clockRef.set(Clock.systemUTC());
    }

    public void offsetTo(Duration duration) {
        clockRef.updateAndGet(clock -> Clock.offset(clock, duration));
    }

    @Override
    public Clock get() {
        return clockRef.get();
    }
}
