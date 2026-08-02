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
 * Tests de FK cruzadas entre esquemas.
 * Verifica que las foreign keys entre esquemas (sales → access, sales → inventory,
 * inventory → access) se aplican correctamente por PostgreSQL.
 *
 * Validates: Requirements 15.6, 1.2
 */
@Transactional
class CrossSchemaForeignKeyTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // =========================================================================
    // FK Violation Tests - INSERT con FK inexistente debe fallar
    // =========================================================================

    @Test
    @DisplayName("sales.cash_registers.branch_id → access.branches: non-existent branch_id should fail")
    void shouldRejectCashRegisterWithNonExistentBranchId() {
        assertThatThrownBy(() ->
                jdbcTemplate.execute(
                        "INSERT INTO sales.cash_registers (branch_id, user_id, status) " +
                        "VALUES (99999, 1, 'OPEN')")
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("sales.account_details.product_id → inventory.products: non-existent product_id should fail")
    void shouldRejectAccountDetailWithNonExistentProductId() {
        // Create prerequisite branch and account
        jdbcTemplate.execute(
                "INSERT INTO access.branches (id, name) VALUES (8001, 'FK Test Branch AD')");
        jdbcTemplate.execute(
                "INSERT INTO sales.accounts (id, branch_id, status, opened_by) " +
                "VALUES (8001, 8001, 'OPEN', 1)");

        assertThatThrownBy(() ->
                jdbcTemplate.execute(
                        "INSERT INTO sales.account_details " +
                        "(account_id, product_id, quantity, unit_price, subtotal, added_by) " +
                        "VALUES (8001, 99999, 2, 15.00, 30.00, 1)")
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("inventory.product_stock.branch_id → access.branches: non-existent branch_id should fail")
    void shouldRejectProductStockWithNonExistentBranchId() {
        // Create prerequisite product
        jdbcTemplate.execute(
                "INSERT INTO inventory.products (id, code, name, cost_price, sale_price) " +
                "VALUES (8001, 'PROD-FK-TEST', 'FK Test Product', 5.00, 10.00)");

        assertThatThrownBy(() ->
                jdbcTemplate.execute(
                        "INSERT INTO inventory.product_stock (product_id, branch_id, current_stock, minimum_stock) " +
                        "VALUES (8001, 99999, 50, 5)")
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    // =========================================================================
    // Valid Cross-Schema FK Tests - INSERT con FK existente debe funcionar
    // =========================================================================

    @Test
    @DisplayName("sales.cash_registers with valid branch_id from access.branches should succeed")
    void shouldAllowCashRegisterWithValidBranchId() {
        jdbcTemplate.execute(
                "INSERT INTO access.branches (id, name) VALUES (8002, 'FK Test Branch CR')");

        jdbcTemplate.execute(
                "INSERT INTO sales.cash_registers (branch_id, user_id, status) " +
                "VALUES (8002, 1, 'OPEN')");

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sales.cash_registers WHERE branch_id = 8002",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("sales.account_details with valid product_id from inventory.products should succeed")
    void shouldAllowAccountDetailWithValidProductId() {
        // Create prerequisite data across schemas
        jdbcTemplate.execute(
                "INSERT INTO access.branches (id, name) VALUES (8003, 'FK Test Branch AD Valid')");
        jdbcTemplate.execute(
                "INSERT INTO inventory.products (id, code, name, cost_price, sale_price) " +
                "VALUES (8003, 'PROD-FK-VALID', 'FK Valid Product', 8.00, 16.00)");
        jdbcTemplate.execute(
                "INSERT INTO sales.accounts (id, branch_id, status, opened_by) " +
                "VALUES (8003, 8003, 'OPEN', 1)");

        jdbcTemplate.execute(
                "INSERT INTO sales.account_details " +
                "(account_id, product_id, quantity, unit_price, subtotal, added_by) " +
                "VALUES (8003, 8003, 3, 16.00, 48.00, 1)");

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sales.account_details WHERE account_id = 8003 AND product_id = 8003",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("inventory.product_stock with valid branch_id from access.branches should succeed")
    void shouldAllowProductStockWithValidBranchId() {
        // Create prerequisite data across schemas
        jdbcTemplate.execute(
                "INSERT INTO access.branches (id, name) VALUES (8004, 'FK Test Branch PS')");
        jdbcTemplate.execute(
                "INSERT INTO inventory.products (id, code, name, cost_price, sale_price) " +
                "VALUES (8004, 'PROD-FK-STOCK', 'FK Stock Product', 12.00, 24.00)");

        jdbcTemplate.execute(
                "INSERT INTO inventory.product_stock (product_id, branch_id, current_stock, minimum_stock) " +
                "VALUES (8004, 8004, 100, 10)");

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM inventory.product_stock WHERE product_id = 8004 AND branch_id = 8004",
                Integer.class);
        assertThat(count).isEqualTo(1);
    }
}
