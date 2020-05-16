package zerodowntimedeployment;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Тест переименования столбца
 */
class E_DeleteColumnTest extends AbstractTest {

    private static Application v1 = new Application(jdbcConnectionPool, "V1", "e-delete-column-test",
            () -> {
                //пишем и читаем старый столбец
                jdbcTemplate.update("insert into test (id, username) values(1,'user1')");
                jdbcTemplate.queryForList("select count(distinct username) from test where id=1");
            });
    private static Application v2_1 = new Application(jdbcConnectionPool, "V2_1", "e-delete-column-test",
            () -> {
                //прекращаем чтение старого столбца, но пишем в него по-прежнему
                jdbcTemplate.update("insert into test (id, username) values(1,'user1')");
                jdbcTemplate.queryForList("select count(*) from test where id=1");
            });
    private static Application v2_2 = new Application(jdbcConnectionPool, "V2_2", "e-delete-column-test",
            () -> {
                //прекращаем запись в старый столбец
                jdbcTemplate.update("insert into test (id) values(1)");
                jdbcTemplate.queryForList("select count(*) from test where id=1");
            });
    private static Application v3 = new Application(jdbcConnectionPool, "V3", "e-delete-column-test",
            () -> {
                //можем удалить столбец, т.к. больше никто в него не пишет и не читает из него
                jdbcTemplate.update("insert into test (id) values(1)");
                jdbcTemplate.queryForList("select count(*) from test where id=1");
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
                {v3},
                {v2_2},
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
