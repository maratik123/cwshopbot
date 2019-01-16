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
package name.maratik.cw.cwshopbot.packer;

import name.maratik.cw.cwshopbot.proto.ReplyData;

import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Log4j2
public class Packer {
    private static final List<Container> PAGED_REQUEST_PACKERS = ImmutableList.of(
        Container.PAGED_REQUEST_BASE_64,
        Container.PAGED_REQUEST_DEFLATE_BASE_64
    );
    private static final Charset JSON_CHARSET = StandardCharsets.UTF_8;
    private static final Charset BASE64_CHARSET = StandardCharsets.US_ASCII;

    public static Optional<String> packData(ReplyData.PagedRequest pagedRequest, int maxSize) {
        log.debug("Packing data: {}", pagedRequest);
        Optional<String> result = PAGED_REQUEST_PACKERS.stream()
            .map(container -> packData(container, pagedRequest))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(packedData -> checkJsonSerializedSizeFit(packedData, maxSize))
            .findFirst();
        log.debug("Packed data result is {}", result);
        return result;
    }

    public static Optional<ReplyData.PagedRequest> unpackData(String packedData) {
        log.debug("Unpacking data: {}", packedData);
        if (packedData.isEmpty()) {
            log.warn("Data is empty");
            return Optional.empty();
        }
        byte[] bytes = packedData.getBytes(BASE64_CHARSET);
        Optional<ReplyData.PagedRequest> result = Container.findByCode(packedData.charAt(0)).flatMap(container ->
            unpackData(container, new ByteArrayInputStream(bytes, 1, bytes.length - 1))
        );
        if (!result.isPresent()) {
            log.warn("Can not unpack data: {}", packedData);
        }
        log.debug("Unpacked result is: {}", result);
        return result;
    }

    private static boolean checkJsonSerializedSizeFit(String data, int maxSize) {
        return data.getBytes(JSON_CHARSET).length <= maxSize;
    }

    private static Optional<String> packData(Container container, ReplyData.PagedRequest pagedRequest) {
        log.debug("Packing data with {}", container);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(64);
            try (OutputStream base64 = Base64.getEncoder().wrap(baos)) {
                container.apply(base64, pagedRequest::writeTo);
            }
            Optional<String> result = Optional.of(container.getCode() + new String(baos.toByteArray(), BASE64_CHARSET));
            log.debug("Packed result: {}", result);
            return result;
        } catch (IOException e) {
            log.error("Can not write data: {}", pagedRequest, e);
            return Optional.empty();
        }
    }

    private static Optional<ReplyData.PagedRequest> unpackData(Container container, ByteArrayInputStream bais) {
        log.debug("Unpacking data with {}", container);
        try (InputStream base64 = Base64.getDecoder().wrap(bais)) {
            return Optional.of(container.apply(base64, ReplyData.PagedRequest::parseFrom));
        } catch (IOException e) {
            log.error("Can not read data", e);
            return Optional.empty();
        }
    }

    private interface OutputConsumer {
        void accept(OutputStream outputStream) throws IOException;
    }

    private interface InputConsumer<T> {
        T accept(InputStream inputStream) throws IOException;
    }

    private enum Container {
        PAGED_REQUEST_DEFLATE_BASE_64('A') {
            void apply(OutputStream outputStream, OutputConsumer dataWriter) throws IOException {
                log.debug("Deflater encoder");
                Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, true);
                try (DeflaterOutputStream deflaterStream = new DeflaterOutputStream(outputStream, deflater, 64, false)) {
                    dataWriter.accept(deflaterStream);
                } finally {
                    deflater.end();
                }
            }

            <T> T apply(InputStream inputStream, InputConsumer<T> dataReader) throws IOException {
                Inflater inflater = new Inflater(true);
                try (InflaterInputStream inflaterStream = new InflaterInputStream(inputStream, inflater, 64)) {
                    return dataReader.accept(inflaterStream);
                } finally {
                    inflater.end();
                }
            }
        },
        PAGED_REQUEST_BASE_64('B') {
            void apply(OutputStream outputStream, OutputConsumer dataWriter) throws IOException {
                log.debug("Plain encoder");
                dataWriter.accept(outputStream);
            }

            <T> T apply(InputStream inputStream, InputConsumer<T> dataReader) throws IOException {
                return dataReader.accept(inputStream);
            }
        };

        private final char code;

        private static final Map<Character, Container> cache = Arrays.stream(values())
            .collect(toImmutableMap(Container::getCode, t -> t));

        Container(char code) {
            this.code = code;
        }

        char getCode() {
            return code;
        }

        static Optional<Container> findByCode(char code) {
            return Optional.ofNullable(cache.get(code));
        }

        abstract void apply(OutputStream outputStream, OutputConsumer dataWriter) throws IOException;

        abstract <T> T apply(InputStream inputStream, InputConsumer<T> dataReader) throws IOException;
    }
}
