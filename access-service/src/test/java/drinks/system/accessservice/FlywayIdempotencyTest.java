package drinks.system.accessservice;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class FlywayIdempotencyTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private Flyway flyway;

    @Test
    void validateShouldPassAfterMigrate() {
        // Migrations already ran during context startup
        assertThatNoException().isThrownBy(() -> flyway.validate());
    }

    @Test
    void migrateShouldBeIdempotent() {
        MigrateResult result = flyway.migrate();
        assertThat(result.migrationsExecuted).isEqualTo(0);
    }
}
