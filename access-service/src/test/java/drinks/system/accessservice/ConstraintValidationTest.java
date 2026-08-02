package drinks.system.accessservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests de validación de constraints de la base de datos.
 * Verifica UNIQUE, CHECK y NOT NULL constraints en las tablas del sistema.
 *
 * Validates: Requirements 15.8, 15.9, 12.4, 6.4
 */
@Transactional
class ConstraintValidationTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // =========================================================================
    // UNIQUE Constraints
    // =========================================================================

    @Test
    @DisplayName("UNIQUE constraint on users.username - duplicate username should fail")
    void shouldRejectDuplicateUsername() {
        jdbcTemplate.execute(
                "INSERT INTO access.users (username, password_hash, email, full_name) " +
                "VALUES ('testuser', 'hash123', 'test1@mail.com', 'Test User 1')");

        assertThatThrownBy(() ->
                jdbcTemplate.execute(
                        "INSERT INTO access.users (username, password_hash, email, full_name) " +
                        "VALUES ('testuser', 'hash456', 'test2@mail.com', 'Test User 2')")
        ).isInstanceOf(DataIntegrityViolationException.class)
         .hasMessageContaining("users_username_key");
    }

    @Test
    @DisplayName("UNIQUE constraint on roles.code - duplicate role code should fail")
    void shouldRejectDuplicateRoleCode() {
        jdbcTemplate.execute(
                "INSERT INTO access.roles (code, name) VALUES ('TEST_ROLE', 'Test Role 1')");

        assertThatThrownBy(() ->
                jdbcTemplate.execute(
                        "INSERT INTO access.roles (code, name) VALUES ('TEST_ROLE', 'Test Role 2')")
        ).isInstanceOf(DataIntegrityViolationException.class)
         .hasMessageContaining("roles_code_key");
    }

    @Test
    @DisplayName("UNIQUE constraint on catalogs (catalog_type, code) - duplicate combination should fail")
    void shouldRejectDuplicateCatalogTypeAndCode() {
        jdbcTemplate.execute(
                "INSERT INTO access.catalogs (catalog_type, code, name) " +
                "VALUES ('TEST_TYPE', 'TEST_CODE', 'Catalog 1')");

        assertThatThrownBy(() ->
                jdbcTemplate.execute(
                        "INSERT INTO access.catalogs (catalog_type, code, name) " +
                        "VALUES ('TEST_TYPE', 'TEST_CODE', 'Catalog 2')")
        ).isInstanceOf(DataIntegrityViolationException.class)
         .hasMessageContaining("catalogs_catalog_type_code_key");
    }

    @Test
    @DisplayName("UNIQUE constraint on product_stock (product_id, branch_id) - duplicate combination should fail")
    void shouldRejectDuplicateProductStockPerBranch() {
        // Create prerequisite branch and product
        jdbcTemplate.execute(
                "INSERT INTO access.branches (id, name) VALUES (9001, 'Test Branch Stock')");
        jdbcTemplate.execute(
                "INSERT INTO inventory.products (id, code, name, cost_price, sale_price) " +
                "VALUES (9001, 'PROD-STOCK-TEST', 'Product Stock Test', 10.00, 20.00)");

        jdbcTemplate.execute(
                "INSERT INTO inventory.product_stock (product_id, branch_id, current_stock, minimum_stock) " +
                "VALUES (9001, 9001, 100, 10)");

        assertThatThrownBy(() ->
                jdbcTemplate.execute(
                        "INSERT INTO inventory.product_stock (product_id, branch_id, current_stock, minimum_stock) " +
                        "VALUES (9001, 9001, 50, 5)")
        ).isInstanceOf(DataIntegrityViolationException.class)
         .hasMessageContaining("product_stock_product_id_branch_id_key");
    }

    // =========================================================================
    // CHECK Constraints
    // =========================================================================

    @Test
    @DisplayName("CHECK constraint on cash_registers.status - invalid status should fail")
    void shouldRejectInvalidCashRegisterStatus() {
        jdbcTemplate.execute(
                "INSERT INTO access.branches (id, name) VALUES (9002, 'Test Branch CR')");

        assertThatThrownBy(() ->
                jdbcTemplate.execute(
                        "INSERT INTO sales.cash_registers (branch_id, user_id, status) " +
                        "VALUES (9002, 1, 'INVALID')")
        ).isInstanceOf(DataIntegrityViolationException.class)
         .hasMessageContaining("cash_registers_status_check");
    }

    @Test
    @DisplayName("CHECK constraint on inventory_movements.movement_type - invalid type should fail")
    void shouldRejectInvalidInventoryMovementType() {
        jdbcTemplate.execute(
                "INSERT INTO access.branches (id, name) VALUES (9003, 'Test Branch IM')");
        jdbcTemplate.execute(
                "INSERT INTO inventory.products (id, code, name, cost_price, sale_price) " +
                "VALUES (9003, 'PROD-MOV-TEST', 'Product Movement Test', 5.00, 10.00)");

        assertThatThrownBy(() ->
                jdbcTemplate.execute(
                        "INSERT INTO inventory.inventory_movements " +
                        "(product_id, branch_id, movement_type, quantity, previous_stock, new_stock, created_by) " +
                        "VALUES (9003, 9003, 'INVALID_TYPE', 10, 0, 10, 1)")
        ).isInstanceOf(DataIntegrityViolationException.class)
         .hasMessageContaining("inventory_movements_movement_type_check");
    }

    @Test
    @DisplayName("CHECK constraint on account_details.quantity - quantity <= 0 should fail")
    void shouldRejectInvalidAccountDetailQuantity() {
        // Create prerequisite data
        jdbcTemplate.execute(
                "INSERT INTO access.branches (id, name) VALUES (9004, 'Test Branch AD')");
        jdbcTemplate.execute(
                "INSERT INTO inventory.products (id, code, name, cost_price, sale_price) " +
                "VALUES (9004, 'PROD-AD-TEST', 'Product AD Test', 5.00, 10.00)");
        jdbcTemplate.execute(
                "INSERT INTO sales.accounts (id, branch_id, status, opened_by) " +
                "VALUES (9004, 9004, 'OPEN', 1)");

        assertThatThrownBy(() ->
                jdbcTemplate.execute(
                        "INSERT INTO sales.account_details " +
                        "(account_id, product_id, quantity, unit_price, subtotal, added_by) " +
                        "VALUES (9004, 9004, 0, 10.00, 0.00, 1)")
        ).isInstanceOf(DataIntegrityViolationException.class)
         .hasMessageContaining("account_details_quantity_check");
    }

    // =========================================================================
    // NOT NULL Constraints
    // =========================================================================

    @Test
    @DisplayName("NOT NULL constraint on branches.name - null name should fail")
    void shouldRejectNullBranchName() {
        assertThatThrownBy(() ->
                jdbcTemplate.execute(
                        "INSERT INTO access.branches (name) VALUES (NULL)")
        ).isInstanceOf(DataIntegrityViolationException.class);
    }
}
