package drinks.system.accessservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests de comportamiento ON DELETE en foreign keys.
 * Verifica que RESTRICT, CASCADE y SET NULL funcionan correctamente
 * según el diseño de la base de datos.
 *
 * Validates: Requirements 15.6
 */
@Transactional
class DeleteBehaviorTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // =========================================================================
    // ON DELETE RESTRICT
    // =========================================================================

    @Test
    @DisplayName("RESTRICT: Cannot delete branch that has cash_registers referencing it")
    void shouldPreventDeletingBranchWithCashRegisters() {
        // Create branch
        jdbcTemplate.execute(
                "INSERT INTO access.branches (id, name) VALUES (8001, 'Branch RESTRICT Test')");

        // Create cash_register referencing the branch
        jdbcTemplate.execute(
                "INSERT INTO sales.cash_registers (branch_id, user_id, status) " +
                "VALUES (8001, 1, 'OPEN')");

        // Attempt to delete branch should fail with RESTRICT
        assertThatThrownBy(() ->
                jdbcTemplate.execute("DELETE FROM access.branches WHERE id = 8001")
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    // =========================================================================
    // ON DELETE CASCADE
    // =========================================================================

    @Test
    @DisplayName("CASCADE: Deleting user cascades deletion to user_roles entries")
    void shouldCascadeDeleteUserRolesWhenUserDeleted() {
        // Create user
        jdbcTemplate.execute(
                "INSERT INTO access.users (id, username, password_hash, email, full_name) " +
                "VALUES (8001, 'cascade_test_user', 'hash123', 'cascade@test.com', 'Cascade Test User')");

        // Create role
        jdbcTemplate.execute(
                "INSERT INTO access.roles (id, code, name) " +
                "VALUES (8001, 'CASCADE_TEST_ROLE', 'Cascade Test Role')");

        // Assign role to user
        jdbcTemplate.execute(
                "INSERT INTO access.user_roles (user_id, role_id) VALUES (8001, 8001)");

        // Verify user_roles entry exists
        Integer countBefore = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM access.user_roles WHERE user_id = 8001", Integer.class);
        assertThat(countBefore).isEqualTo(1);

        // Delete user
        jdbcTemplate.execute("DELETE FROM access.users WHERE id = 8001");

        // Verify user_roles entry was cascaded
        Integer countAfter = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM access.user_roles WHERE user_id = 8001", Integer.class);
        assertThat(countAfter).isEqualTo(0);
    }

    // =========================================================================
    // ON DELETE SET NULL
    // =========================================================================

    @Test
    @DisplayName("SET NULL: Deleting branch sets users.branch_id to NULL")
    void shouldSetNullOnUserBranchIdWhenBranchDeleted() {
        // Create branch (one without RESTRICT references like cash_registers)
        jdbcTemplate.execute(
                "INSERT INTO access.branches (id, name) VALUES (8002, 'Branch SET NULL Test')");

        // Create user with branch_id referencing the branch
        jdbcTemplate.execute(
                "INSERT INTO access.users (id, username, password_hash, email, full_name, branch_id) " +
                "VALUES (8002, 'setnull_test_user', 'hash456', 'setnull@test.com', 'SetNull Test User', 8002)");

        // Verify branch_id is set before deletion
        Long branchIdBefore = jdbcTemplate.queryForObject(
                "SELECT branch_id FROM access.users WHERE id = 8002", Long.class);
        assertThat(branchIdBefore).isEqualTo(8002L);

        // Delete the branch
        jdbcTemplate.execute("DELETE FROM access.branches WHERE id = 8002");

        // Verify user's branch_id is now NULL
        Long branchIdAfter = jdbcTemplate.queryForObject(
                "SELECT branch_id FROM access.users WHERE id = 8002", Long.class);
        assertThat(branchIdAfter).isNull();
    }
}
