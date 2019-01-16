package name.maratik.cw.cwshopbot.application.db;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Service
@Log4j2
public class DbCleaner {

    private final JdbcTemplate jdbcTemplate;
    private final ResourceLoader resourceLoader;

    public DbCleaner(JdbcTemplate jdbcTemplate, ResourceLoader resourceLoader) {
        this.jdbcTemplate = jdbcTemplate;
        this.resourceLoader = resourceLoader;
    }

    @Transactional
    public void clearDb() {
        log.info("Clearing DB");
        jdbcTemplate.execute((Connection con) -> {
            ScriptUtils.executeSqlScript(con, resourceLoader.getResource("classpath:/sql/cleanup_tables.sql"));
            return null;
        });
    }
}
