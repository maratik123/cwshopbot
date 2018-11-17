package name.maratik.cw.cwshopbot.model;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class PagedResponse<T> {
    private final T response;
    private final long count;

    public PagedResponse(T response, long count) {
        this.response = response;
        this.count = count;
    }

    public T getResponse() {
        return response;
    }

    public long getCount() {
        return count;
    }
}
