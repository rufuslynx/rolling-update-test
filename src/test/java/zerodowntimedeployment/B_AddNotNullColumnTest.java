package zerodowntimedeployment;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Тест добавления not null-столбца
 */
class B_AddNotNullColumnTest extends AbstractTest {

    private static Application v1 = new Application(jdbcConnectionPool, "V1", "b-add-non-null-column",
            () -> {
                jdbcTemplate.update("insert into test (id) values(1)");
                jdbcTemplate.queryForList("select count(*) from test where id='1'");
            });
    private static Application v2_1 = new Application(jdbcConnectionPool, "V2_1", "b-add-non-null-column",
            () -> {
                //пишем с учетом значения по умолчанию
                jdbcTemplate.update("insert into test (id, username) values(1, coalesce('usernamexx','default_user'))");
                jdbcTemplate.queryForList("select count(*) from test where username='usernamexx'");
                //читаем с учетом значения по умолчанию
                jdbcTemplate.queryForList("select distinct coalesce(username,'default_user') from test");
            });
    private static Application v2_2 = new Application(jdbcConnectionPool, "V2_2", "b-add-non-null-column",
            () -> {
                //пишем с учетом значения по умолчанию
                jdbcTemplate.update("insert into test (id, username) values(1, coalesce('usernamexx','default_user'))");
                jdbcTemplate.queryForList("select count(*) from test where username='usernamexx'");
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
                //{v1}, //если включим, получим ошибку
        };
    }

    @ParameterizedTest
    @MethodSource("testRollingUpdate")
    void testRollingUpdate(Application app) {
        app.migrate();
        app.doWork();
    }

}
