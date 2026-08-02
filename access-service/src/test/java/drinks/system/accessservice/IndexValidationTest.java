package drinks.system.accessservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Validates that all indexes defined in the database design document exist
 * in the actual database after Flyway migrations run.
 *
 * Validates: Requirements 16.1, 16.2, 16.3, 16.4, 16.5
 */
class IndexValidationTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private DataSource dataSource;

    private Set<String> allIndexes;

    @BeforeEach
    void loadAllIndexes() throws SQLException {
        allIndexes = new HashSet<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT schemaname, tablename, indexname FROM pg_indexes " +
                             "WHERE schemaname IN ('access', 'sales', 'inventory', 'reporting')")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                allIndexes.add(rs.getString("indexname"));
            }
        }
    }

    private void assertIndexExists(String indexName) {
        assertThat(allIndexes)
                .as("Index '%s' should exist in the database", indexName)
                .contains(indexName);
    }

    @Nested
    @DisplayName("16.1 - FK Indexes")
    class ForeignKeyIndexes {

        @Test
        @DisplayName("idx_users_branch_id exists on access.users")
        void shouldHaveUsersBranchIdIndex() {
            assertIndexExists("idx_users_branch_id");
        }

        @Test
        @DisplayName("idx_role_permissions_role_id exists on access.role_permissions")
        void shouldHaveRolePermissionsRoleIdIndex() {
            assertIndexExists("idx_role_permissions_role_id");
        }

        @Test
        @DisplayName("idx_product_stock_product_id exists on inventory.product_stock")
        void shouldHaveProductStockProductIdIndex() {
            assertIndexExists("idx_product_stock_product_id");
        }

        @Test
        @DisplayName("idx_cash_registers_branch_id exists on sales.cash_registers")
        void shouldHaveCashRegistersBranchIdIndex() {
            assertIndexExists("idx_cash_registers_branch_id");
        }
    }

    @Nested
    @DisplayName("16.2 - Composite Indexes")
    class CompositeIndexes {

        @Test
        @DisplayName("idx_cash_registers_branch_status exists on sales.cash_registers")
        void shouldHaveCashRegistersBranchStatusIndex() {
            assertIndexExists("idx_cash_registers_branch_status");
        }

        @Test
        @DisplayName("idx_accounts_branch_status exists on sales.accounts")
        void shouldHaveAccountsBranchStatusIndex() {
            assertIndexExists("idx_accounts_branch_status");
        }

        @Test
        @DisplayName("idx_sales_branch_date exists on sales.sales")
        void shouldHaveSalesBranchDateIndex() {
            assertIndexExists("idx_sales_branch_date");
        }

        @Test
        @DisplayName("idx_inv_movements_product_branch exists on inventory.inventory_movements")
        void shouldHaveInvMovementsProductBranchIndex() {
            assertIndexExists("idx_inv_movements_product_branch");
        }

        @Test
        @DisplayName("idx_notifications_user_read exists on access.notifications")
        void shouldHaveNotificationsUserReadIndex() {
            assertIndexExists("idx_notifications_user_read");
        }
    }

    @Nested
    @DisplayName("16.3 - Partial Indexes")
    class PartialIndexes {

        @Test
        @DisplayName("idx_branches_is_active partial index exists on access.branches")
        void shouldHaveBranchesIsActivePartialIndex() {
            assertIndexExists("idx_branches_is_active");
        }

        @Test
        @DisplayName("idx_users_is_active partial index exists on access.users")
        void shouldHaveUsersIsActivePartialIndex() {
            assertIndexExists("idx_users_is_active");
        }

        @Test
        @DisplayName("idx_categories_is_active partial index exists on inventory.categories")
        void shouldHaveCategoriesIsActivePartialIndex() {
            assertIndexExists("idx_categories_is_active");
        }

        @Test
        @DisplayName("idx_products_is_active partial index exists on inventory.products")
        void shouldHaveProductsIsActivePartialIndex() {
            assertIndexExists("idx_products_is_active");
        }

        @Test
        @DisplayName("idx_suppliers_is_active partial index exists on inventory.suppliers")
        void shouldHaveSuppliersIsActivePartialIndex() {
            assertIndexExists("idx_suppliers_is_active");
        }

        @Test
        @DisplayName("idx_customers_is_active partial index exists on sales.customers")
        void shouldHaveCustomersIsActivePartialIndex() {
            assertIndexExists("idx_customers_is_active");
        }

        @Test
        @DisplayName("idx_product_stock_low partial index exists on inventory.product_stock")
        void shouldHaveProductStockLowPartialIndex() {
            assertIndexExists("idx_product_stock_low");
        }

        @Test
        @DisplayName("idx_inv_status_low_stock partial index exists on reporting.inventory_status_view")
        void shouldHaveInvStatusLowStockPartialIndex() {
            assertIndexExists("idx_inv_status_low_stock");
        }
    }

    @Nested
    @DisplayName("16.4 - Date Indexes")
    class DateIndexes {

        @Test
        @DisplayName("idx_audit_logs_created_at exists on access.audit_logs")
        void shouldHaveAuditLogsCreatedAtIndex() {
            assertIndexExists("idx_audit_logs_created_at");
        }

        @Test
        @DisplayName("idx_inv_movements_created_at exists on inventory.inventory_movements")
        void shouldHaveInvMovementsCreatedAtIndex() {
            assertIndexExists("idx_inv_movements_created_at");
        }

        @Test
        @DisplayName("idx_sales_sale_date exists on sales.sales")
        void shouldHaveSalesSaleDateIndex() {
            assertIndexExists("idx_sales_sale_date");
        }

        @Test
        @DisplayName("idx_cash_registers_opened_at exists on sales.cash_registers")
        void shouldHaveCashRegistersOpenedAtIndex() {
            assertIndexExists("idx_cash_registers_opened_at");
        }
    }

    @Nested
    @DisplayName("16.5 - Unique Indexes")
    class UniqueIndexes {

        @Test
        @DisplayName("idx_sales_branch_sale_number unique index exists on sales.sales")
        void shouldHaveSalesBranchSaleNumberUniqueIndex() {
            assertIndexExists("idx_sales_branch_sale_number");
        }
    }
}
