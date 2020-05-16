package zerodowntimedeployment;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Тест переименования столбца
 */
class C_RenameColumnTest extends AbstractTest {

    private static Application v1 = new Application(jdbcConnectionPool, "V1", "c-rename-column",
            () -> {
                //пишем и читаем старый столбец
                jdbcTemplate.update("insert into test (id) values(1)");
                jdbcTemplate.queryForList("select count(*) from test where id='1'");
            });
    private static Application v2_1 = new Application(jdbcConnectionPool, "V2_1", "c-rename-column",
            () -> {
                //пишем в старый и новый столбец, читаем только старый
                jdbcTemplate.update("insert into test (id, idd) values(1, 1)");
                jdbcTemplate.queryForList("select count(*) from test where id='1'");
            });
    private static Application v2_2 = new Application(jdbcConnectionPool, "V2_2", "c-rename-column",
            () -> {
                //пишем в старый и новый столбец, читаем только новый
                jdbcTemplate.update("insert into test (id, idd) values(1, 1)");
                jdbcTemplate.queryForList("select count(*) from test where idd='1'");
            });
    private static Application v2_3 = new Application(jdbcConnectionPool, "V2_3", "c-rename-column",
            () -> {
                //пишем и читаем только новые столбцы
                jdbcTemplate.update("insert into test (idd) values(1)");
                jdbcTemplate.queryForList("select count(*) from test where idd='1'");
            });
    private static Application v3 = new Application(jdbcConnectionPool, "V3", "c-rename-column",
            () -> {
                //старого столбца больше нет
                jdbcTemplate.update("insert into test (idd) values(1)");
                jdbcTemplate.queryForList("select count(*) from test where idd='1'");
            });

    private static Object[][] testRollingUpdate() {
        return new Object[][]{
                {v1},
                {v2_1},
                {v1},
                {v2_1},
                {v2_2},
                {v2_1},
                {v2_2},
                {v2_3},
                {v2_2},
                {v2_3},
                {v3},
                {v2_3},
                {v3},
        };
    }

    @ParameterizedTest
    @MethodSource("testRollingUpdate")
    void testRollingUpdate(Application app) {
        app.migrate();
        app.doWork();
    }

}
