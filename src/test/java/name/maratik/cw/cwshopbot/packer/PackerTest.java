package name.maratik.cw.cwshopbot.packer;

import name.maratik.cw.cwshopbot.proto.ReplyData;

import org.junit.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class PackerTest {

    @Test
    public void packUnPackData() {
        ReplyData.PagedRequest expectedPagedRequest = ReplyData.PagedRequest.newBuilder()
            .setRequestType(ReplyData.RequestType.STATS_USERS)
            .setPage(1)
            .setQuery("test")
            .build();

        Optional<String> packedData = Packer.packData(expectedPagedRequest, 64);
        assertTrue(packedData.isPresent());

        Optional<ReplyData.PagedRequest> actual = Packer.unpackData(packedData.get());
        assertTrue(actual.isPresent());

        ReplyData.PagedRequest actualPagedRequest = actual.get();

        assertThat(actualPagedRequest, samePropertyValuesAs(expectedPagedRequest));
    }

    @Test
    public void unpackDataBackwardCompatibility() {
        Stream.of("A42AUYJRiKUktLgEA", "BCAEQARoEdGVzdA==").forEach(packedData -> {
            Optional<ReplyData.PagedRequest> actual = Packer.unpackData(packedData);
            assertTrue(actual.isPresent());

            ReplyData.PagedRequest actualPagedRequest = actual.get();
            assertEquals(ReplyData.RequestType.STATS_USERS, actualPagedRequest.getRequestType());
            assertEquals(1, actualPagedRequest.getPage());
            assertEquals("test", actualPagedRequest.getQuery());
        });
    }
}
