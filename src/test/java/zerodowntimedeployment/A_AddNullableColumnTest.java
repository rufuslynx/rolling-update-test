package zerodowntimedeployment;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Тест добавления nullable-столбца
 * Не содержит промежуточных версий для совместимой миграции,
 * совместимая миграция выполняется в один шаг
 */
class A_AddNullableColumnTest extends AbstractTest {

    private static Application v1 = new Application(jdbcConnectionPool, "V1", "a-add-column-test",
            () -> {
                jdbcTemplate.update("insert into test (id) values(1)");
                jdbcTemplate.queryForList("select count(*) from test where id='1'");
            });
    private static Application v2_1 = new Application(jdbcConnectionPool, "V2_1", "a-add-column-test",
            () -> {
                jdbcTemplate.update("insert into test (id, username) values(1,'usernamexx')");
                jdbcTemplate.queryForList("select count(*) from test where username='usernamexx'");
            });

    private static Object[][] testRollingUpdate() {
        return new Object[][]{
                {v1},
                {v2_1},
                {v1},
                {v2_1},
        };
    }

    @ParameterizedTest
    @MethodSource("testRollingUpdate")
    void testRollingUpdate(Application app) {
        app.migrate();
        app.doWork();
    }
}
