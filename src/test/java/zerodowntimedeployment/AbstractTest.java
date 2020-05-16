package zerodowntimedeployment;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class AbstractTest {
    static JdbcConnectionPool jdbcConnectionPool = JdbcConnectionPool.create("jdbc:h2:~/test", "sa", "sa");
    static JdbcTemplate jdbcTemplate = new JdbcTemplate(jdbcConnectionPool);

    @BeforeAll
    static void beforeAll() {
        jdbcTemplate.execute("DROP ALL OBJECTS DELETE FILES;");
    }

    static final class Application {
        private final Flyway flyway;
        private final Runnable work;
        private final String versionName;

        Application(DataSource dataSource, String versionName, String dir, Runnable work) {
            this.work = work;
            this.versionName = versionName;
            this.flyway = new Flyway(new FluentConfiguration().dataSource(dataSource)
                    .locations("classpath:/" + dir + "/" + versionName)
                    .baselineOnMigrate(true)
                    .baselineDescription(versionName));
        }

        void migrate() {
            flyway.migrate();
        }

        void doWork() {
            System.out.println("Работает версия: " + versionName);
            work.run();
        }
    }
}
