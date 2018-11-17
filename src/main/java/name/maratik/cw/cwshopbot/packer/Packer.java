package name.maratik.cw.cwshopbot.packer;

import name.maratik.cw.cwshopbot.proto.ReplyData;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class Packer {
    private static final Logger logger = LogManager.getLogger(Packer.class);
    private static final List<Container> PAGED_REQUEST_PACKERS = ImmutableList.of(
        Container.PAGED_REQUEST_BASE_64,
        Container.PAGED_REQUEST_DEFLATE_BASE_64
    );
    private static final Charset JSON_CHARSET = StandardCharsets.UTF_8;
    private static final Charset BASE64_CHARSET = StandardCharsets.US_ASCII;

    public static Optional<String> packData(ReplyData.PagedRequest pagedRequest, int maxSize) {
        logger.debug("Packing data: {}", pagedRequest);
        Optional<String> result = PAGED_REQUEST_PACKERS.stream()
            .map(container -> packData(container, pagedRequest))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(packedData -> checkJsonSerializedSizeFit(packedData, maxSize))
            .findFirst();
        logger.debug("Packed data result is {}", result);
        return result;
    }

    public static Optional<ReplyData.PagedRequest> unpackData(String packedData) {
        logger.debug("Unpacking data: {}", packedData);
        if (packedData.isEmpty()) {
            logger.warn("Data is empty");
            return Optional.empty();
        }
        byte[] bytes = packedData.getBytes(BASE64_CHARSET);
        Optional<ReplyData.PagedRequest> result = Container.findByCode(packedData.charAt(0)).flatMap(container ->
            unpackData(container, new ByteArrayInputStream(bytes, 1, bytes.length - 1))
        );
        if (!result.isPresent()) {
            logger.warn("Can not unpack data: {}", packedData);
        }
        logger.debug("Unpacked result is: {}", result);
        return result;
    }

    private static boolean checkJsonSerializedSizeFit(String data, int maxSize) {
        return data.getBytes(JSON_CHARSET).length <= maxSize;
    }

    private static Optional<String> packData(Container container, ReplyData.PagedRequest pagedRequest) {
        logger.debug("Packing data with {}", container);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(64);
            try (OutputStream base64 = Base64.getEncoder().wrap(baos)) {
                container.apply(base64, pagedRequest::writeTo);
            }
            Optional<String> result = Optional.of(container.getCode() + new String(baos.toByteArray(), BASE64_CHARSET));
            logger.debug("Packed result: {}", result);
            return result;
        } catch (IOException e) {
            logger.error("Can not write data: {}", pagedRequest, e);
            return Optional.empty();
        }
    }

    private static Optional<ReplyData.PagedRequest> unpackData(Container container, ByteArrayInputStream bais) {
        logger.debug("Unpacking data with {}", container);
        try (InputStream base64 = Base64.getDecoder().wrap(bais)) {
            return Optional.of(container.apply(base64, ReplyData.PagedRequest::parseFrom));
        } catch (IOException e) {
            logger.error("Can not read data", e);
            return Optional.empty();
        }
    }

    private interface OutputConsumer {
        void accept(OutputStream outputStream) throws IOException;
    }

    private interface OutputBiConsumer {
        void accept(OutputStream outputStream, OutputConsumer dataWriter) throws IOException;
    }

    private interface InputConsumer<T> {
        T accept(InputStream inputStream) throws IOException;
    }

    private interface InputBiConsumer<T> {
        T accept(InputStream inputStream, InputConsumer<T> inputConsumer) throws IOException;
    }

    private enum Container {
        PAGED_REQUEST_DEFLATE_BASE_64('A', Container::applyDeflater, Container::applyInflater),
        PAGED_REQUEST_BASE_64('B', Container::justApply, Container::justApply);

        private final char code;
        private final OutputBiConsumer outputBiConsumer;
        private final InputBiConsumer<?> inputBiConsumer;

        private static final Map<Character, Container> cache = Arrays.stream(values())
            .collect(ImmutableMap.toImmutableMap(Container::getCode, container -> container));

        Container(char code, OutputBiConsumer outputBiConsumer, InputBiConsumer inputBiConsumer) {
            this.code = code;
            this.outputBiConsumer = outputBiConsumer;
            this.inputBiConsumer = inputBiConsumer;
        }

        char getCode() {
            return code;
        }

        static Optional<Container> findByCode(char code) {
            return Optional.ofNullable(cache.get(code));
        }

        void apply(OutputStream outputStream, OutputConsumer dataWriter) throws IOException {
            outputBiConsumer.accept(outputStream, dataWriter);
        }

        <T> T apply(InputStream inputStream, InputConsumer<T> dataReader) throws IOException {
            @SuppressWarnings("unchecked")
            InputBiConsumer<T> inputBiConsumer = (InputBiConsumer<T>) this.inputBiConsumer;
            return inputBiConsumer.accept(inputStream, dataReader);
        }

        private static void applyDeflater(OutputStream outputStream, OutputConsumer dataWriter) throws IOException {
            logger.debug("Deflater encoder");
            Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, true);
            try (DeflaterOutputStream deflaterStream = new DeflaterOutputStream(outputStream, deflater, 64, false)) {
                dataWriter.accept(deflaterStream);
            } finally {
                deflater.end();
            }
        }

        private static void justApply(OutputStream outputStream, OutputConsumer dataWriter) throws IOException {
            logger.debug("Plain encoder");
            dataWriter.accept(outputStream);
        }

        private static <T> T applyInflater(InputStream inputStream, InputConsumer<T> dataReader) throws IOException {
            Inflater inflater = new Inflater(true);
            try (InflaterInputStream inflaterStream = new InflaterInputStream(inputStream, inflater, 64)) {
                return dataReader.accept(inflaterStream);
            } finally {
                inflater.end();
            }
        }

        private static <T> T justApply(InputStream inputStream, InputConsumer<T> dataReader) throws IOException {
            return dataReader.accept(inputStream);
        }
    }
}
