package drinks.system.accessservice;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class FlywayMigrationTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private Flyway flyway;

    @Test
    void shouldHaveNoPendingMigrations() {
        MigrationInfo[] pending = flyway.info().pending();
        assertThat(pending).isEmpty();
    }

    @Test
    void shouldHaveAllMigrationsApplied() {
        MigrationInfo[] applied = flyway.info().applied();
        assertThat(applied).isNotEmpty();
        assertThat(applied).hasSize(28);
    }

    @Test
    void shouldHaveNoFailedMigrations() {
        MigrationInfo[] all = flyway.info().all();
        for (MigrationInfo info : all) {
            assertThat(info.getState())
                    .as("Migration %s should not be in FAILED state", info.getDescription())
                    .isNotEqualTo(MigrationState.FAILED);
        }
    }

    @Test
    void shouldCreateSchemasInCorrectOrder() {
        MigrationInfo[] all = flyway.info().all();

        // Find migrations by version
        MigrationInfo v0 = findByVersion(all, "0");
        MigrationInfo v1 = findByVersion(all, "1");
        MigrationInfo v9 = findByVersion(all, "9");
        MigrationInfo v8 = findByVersion(all, "8");
        MigrationInfo v15 = findByVersion(all, "15");
        MigrationInfo v14 = findByVersion(all, "14");

        assertThat(v0).as("V0 (schemas) should exist").isNotNull();
        assertThat(v1).as("V1 (access tables) should exist").isNotNull();
        assertThat(v9).as("V9 (inventory) should exist").isNotNull();
        assertThat(v15).as("V15 (sales) should exist").isNotNull();

        // V0 (schemas) runs before V1 (access tables)
        assertThat(v0.getInstalledRank())
                .as("V0 (schemas) should run before V1 (access tables)")
                .isLessThan(v1.getInstalledRank());

        // V9 (inventory) runs after V8 (access complete)
        assertThat(v9.getInstalledRank())
                .as("V9 (inventory) should run after V8 (access complete)")
                .isGreaterThan(v8.getInstalledRank());

        // V15 (sales) runs after V14 (inventory complete)
        assertThat(v15.getInstalledRank())
                .as("V15 (sales) should run after V14 (inventory complete)")
                .isGreaterThan(v14.getInstalledRank());
    }

    private MigrationInfo findByVersion(MigrationInfo[] migrations, String version) {
        for (MigrationInfo info : migrations) {
            if (info.getVersion() != null && info.getVersion().toString().equals(version)) {
                return info;
            }
        }
        return null;
    }
}
