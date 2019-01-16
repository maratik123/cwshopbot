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
package name.maratik.cw.cwshopbot.application.db;

import com.google.common.collect.ImmutableList;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.distribution.GenericVersion;
import de.flapdoodle.embed.process.distribution.IVersion;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.store.PostgresArtifactStoreBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import ru.yandex.qatools.embed.postgresql.Command;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresDownloadConfigBuilder;
import ru.yandex.qatools.embed.postgresql.config.RuntimeConfigBuilder;
import ru.yandex.qatools.embed.postgresql.ext.LogWatchStreamProcessor;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;

import static java.util.Collections.singleton;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Log4j2
@Service
public class PostgresEmbedded {

    private volatile PostgresProcess postgresProcess;
    private volatile PostgresConfig postgresConfig;
    private volatile PostgresExecutable postgresExecutable;
    private static final IVersion PGAAS_VERSION = new GenericVersion("11.1-1");

    private final ResourceLoader resourceLoader;

    public PostgresEmbedded(@Value("${cwshopbot.db.username}") String username,
                            @Value("${cwshopbot.db.password}") String password,
                            ResourceLoader resourceLoader) throws IOException {
        this.resourceLoader = resourceLoader;

        String dbName = "cwshopbot_unittest";

        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getInstance(runtimeConfig());

        postgresConfig = new PostgresConfig(PGAAS_VERSION,
            new AbstractPostgresConfig.Net(),
            new AbstractPostgresConfig.Storage(dbName),
            new AbstractPostgresConfig.Timeout(),
            new AbstractPostgresConfig.Credentials(username, password)
        );

        postgresConfig.withAdditionalInitDbParams(ImmutableList.of(
            "-E", "UTF-8",
            "--locale=en_US.UTF-8",
            "--lc-collate=en_US.UTF-8",
            "--lc-ctype=en_US.UTF-8"
        ));
        postgresExecutable = runtime.prepare(postgresConfig);
        postgresProcess = postgresExecutable.start();
    }

    private static final SimpleFileVisitor<Path> DELETING_FILE_VISITOR = new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            FileVisitResult result = super.visitFile(file, attrs);
            Files.delete(file);
            return result;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            FileVisitResult result = super.postVisitDirectory(dir, exc);
            Files.delete(dir);
            return result;
        }
    };

    @PreDestroy
    private void stop() throws IOException {
        try {
            if (postgresProcess != null) {
                postgresProcess.stop();
            }
        } finally {
            try {
                if (postgresConfig != null) {
                    cleanupDirectoriesAfterRun(postgresConfig.storage().dbDir().toPath());
                }
            } finally {
                if (postgresExecutable != null) {
                    cleanupDirectoriesAfterRun(postgresExecutable.getFile().baseDir().toPath());
                }
            }
        }
    }

    private IRuntimeConfig runtimeConfig() throws IOException {
        LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor(
            "started", singleton("failed"), new Log4j2Processor()
        );

        return new RuntimeConfigBuilder()
            .defaults(Command.Postgres)
            .artifactStore(new PostgresArtifactStoreBuilder()
                .defaults(Command.Postgres)
                .download(new PostgresDownloadConfigBuilder()
                    .defaultsForCommand(Command.Postgres)
                    .artifactStorePath(new DistPath())
                    .build()
                )
            )
            .processOutput(new ProcessOutput(logWatch, logWatch, logWatch)).build();
    }

    private static void cleanupDirectoriesAfterRun(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walkFileTree(path, DELETING_FILE_VISITOR);
        }
    }

    public String getUrl() {
        return String.format("jdbc:postgresql://%s:%d/%s",
            postgresConfig.net().host(),
            postgresConfig.net().port(),
            postgresConfig.storage().dbName()
        );
    }

    private static class Log4j2Processor implements IStreamProcessor {
        private static final Pattern EOL = Pattern.compile("[\r\n]+");

        @Override
        public void process(String block) {
            log.debug("{}", () -> EOL.matcher(block).replaceAll(" ").trim());
        }

        @Override
        public void onProcessed() {
        }
    }

    private class DistPath implements IDirectory {
        private final File distDir;

        private DistPath() throws IOException {
            distDir = resourceLoader.getResource(
                "classpath:/name/maratik/cw/cwshopbot/dist"
            ).getFile();
        }

        @Override
        public File asFile() {
            return distDir;
        }

        @Override
        public boolean isGenerated() {
            return false;
        }
    }
}
