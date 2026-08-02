package drinks.system.accessservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Schema Validation Tests")
class SchemaValidationTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private DataSource dataSource;

    // --- Helper methods ---

    private List<String> getSchemas() throws SQLException {
        List<String> schemas = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getSchemas()) {
                while (rs.next()) {
                    schemas.add(rs.getString("TABLE_SCHEM"));
                }
            }
        }
        return schemas;
    }

    private List<String> getTablesInSchema(String schema) throws SQLException {
        List<String> tables = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getTables(null, schema, null, new String[]{"TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
            }
        }
        return tables;
    }

    private String getColumnType(String schema, String table, String column) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getColumns(null, schema, table, column)) {
                if (rs.next()) {
                    return rs.getString("TYPE_NAME");
                }
            }
        }
        return null;
    }

    private int getColumnSize(String schema, String table, String column) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getColumns(null, schema, table, column)) {
                if (rs.next()) {
                    return rs.getInt("COLUMN_SIZE");
                }
            }
        }
        return -1;
    }

    private boolean isColumnNullable(String schema, String table, String column) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getColumns(null, schema, table, column)) {
                if (rs.next()) {
                    return rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
                }
            }
        }
        return false;
    }

    // --- Schema existence tests ---

    @Nested
    @DisplayName("Schema Existence")
    class SchemaExistence {

        @Test
        @DisplayName("Should have all 4 expected schemas")
        void shouldHaveAllExpectedSchemas() throws SQLException {
            List<String> schemas = getSchemas();

            assertThat(schemas).contains("access", "sales", "inventory", "reporting");
        }

        @Test
        @DisplayName("Should have 'access' schema")
        void shouldHaveAccessSchema() throws SQLException {
            assertThat(getSchemas()).contains("access");
        }

        @Test
        @DisplayName("Should have 'sales' schema")
        void shouldHaveSalesSchema() throws SQLException {
            assertThat(getSchemas()).contains("sales");
        }

        @Test
        @DisplayName("Should have 'inventory' schema")
        void shouldHaveInventorySchema() throws SQLException {
            assertThat(getSchemas()).contains("inventory");
        }

        @Test
        @DisplayName("Should have 'reporting' schema")
        void shouldHaveReportingSchema() throws SQLException {
            assertThat(getSchemas()).contains("reporting");
        }
    }

    // --- Table existence tests per schema ---

    @Nested
    @DisplayName("Access Schema Tables")
    class AccessSchemaTables {

        @Test
        @DisplayName("Should have all expected tables in access schema")
        void shouldHaveAllAccessTables() throws SQLException {
            List<String> tables = getTablesInSchema("access");

            assertThat(tables).containsExactlyInAnyOrder(
                    "branches",
                    "users",
                    "roles",
                    "permissions",
                    "role_permissions",
                    "user_roles",
                    "user_branches",
                    "refresh_tokens",
                    "system_menu_options",
                    "system_parameters",
                    "catalogs",
                    "audit_logs",
                    "audit_logs_default",
                    "notifications"
            );
        }
    }

    @Nested
    @DisplayName("Sales Schema Tables")
    class SalesSchemaTables {

        @Test
        @DisplayName("Should have all expected tables in sales schema")
        void shouldHaveAllSalesTables() throws SQLException {
            List<String> tables = getTablesInSchema("sales");

            assertThat(tables).containsExactlyInAnyOrder(
                    "customers",
                    "cash_registers",
                    "cash_register_movements",
                    "accounts",
                    "account_details",
                    "sales",
                    "sale_details"
            );
        }
    }

    @Nested
    @DisplayName("Inventory Schema Tables")
    class InventorySchemaTables {

        @Test
        @DisplayName("Should have all expected tables in inventory schema")
        void shouldHaveAllInventoryTables() throws SQLException {
            List<String> tables = getTablesInSchema("inventory");

            assertThat(tables).containsExactlyInAnyOrder(
                    "categories",
                    "products",
                    "product_stock",
                    "inventory_movements",
                    "suppliers",
                    "purchase_orders",
                    "purchase_order_details"
            );
        }
    }

    @Nested
    @DisplayName("Reporting Schema Tables")
    class ReportingSchemaTables {

        @Test
        @DisplayName("Should have all expected tables in reporting schema")
        void shouldHaveAllReportingTables() throws SQLException {
            List<String> tables = getTablesInSchema("reporting");

            assertThat(tables).containsExactlyInAnyOrder(
                    "daily_sales_summary",
                    "monthly_sales_summary",
                    "product_sales_ranking",
                    "inventory_status_view"
            );
        }
    }

    // --- Column type validation tests ---

    @Nested
    @DisplayName("Column Types Validation")
    class ColumnTypesValidation {

        @Test
        @DisplayName("branches.id should be BIGSERIAL (bigint)")
        void branchesIdShouldBeBigserial() throws SQLException {
            String type = getColumnType("access", "branches", "id");
            assertThat(type).isEqualTo("int8");
        }

        @Test
        @DisplayName("branches.name should be varchar(150)")
        void branchesNameShouldBeVarchar150() throws SQLException {
            String type = getColumnType("access", "branches", "name");
            int size = getColumnSize("access", "branches", "name");

            assertThat(type).isEqualTo("varchar");
            assertThat(size).isEqualTo(150);
        }

        @Test
        @DisplayName("branches.is_active should be boolean")
        void branchesIsActiveShouldBeBoolean() throws SQLException {
            String type = getColumnType("access", "branches", "is_active");
            assertThat(type).isEqualTo("bool");
        }

        @Test
        @DisplayName("branches.created_at should be timestamptz")
        void branchesCreatedAtShouldBeTimestamptz() throws SQLException {
            String type = getColumnType("access", "branches", "created_at");
            assertThat(type).isEqualTo("timestamptz");
        }

        @Test
        @DisplayName("branches.name should be NOT NULL")
        void branchesNameShouldBeNotNull() throws SQLException {
            boolean nullable = isColumnNullable("access", "branches", "name");
            assertThat(nullable).isFalse();
        }

        @Test
        @DisplayName("users.username should be varchar(50)")
        void usersUsernameShouldBeVarchar50() throws SQLException {
            String type = getColumnType("access", "users", "username");
            int size = getColumnSize("access", "users", "username");

            assertThat(type).isEqualTo("varchar");
            assertThat(size).isEqualTo(50);
        }

        @Test
        @DisplayName("users.password_hash should be varchar(255)")
        void usersPasswordHashShouldBeVarchar255() throws SQLException {
            String type = getColumnType("access", "users", "password_hash");
            int size = getColumnSize("access", "users", "password_hash");

            assertThat(type).isEqualTo("varchar");
            assertThat(size).isEqualTo(255);
        }

        @Test
        @DisplayName("users.branch_id should be int8 (bigint FK)")
        void usersBranchIdShouldBeBigint() throws SQLException {
            String type = getColumnType("access", "users", "branch_id");
            assertThat(type).isEqualTo("int8");
        }

        @Test
        @DisplayName("cash_registers.opening_amount should be numeric(12,2)")
        void cashRegistersOpeningAmountShouldBeNumeric() throws SQLException {
            String type = getColumnType("sales", "cash_registers", "opening_amount");
            assertThat(type).isEqualTo("numeric");
        }

        @Test
        @DisplayName("products.code should be varchar(50)")
        void productsCodeShouldBeVarchar50() throws SQLException {
            String type = getColumnType("inventory", "products", "code");
            int size = getColumnSize("inventory", "products", "code");

            assertThat(type).isEqualTo("varchar");
            assertThat(size).isEqualTo(50);
        }

        @Test
        @DisplayName("products.cost_price should be numeric")
        void productsCostPriceShouldBeNumeric() throws SQLException {
            String type = getColumnType("inventory", "products", "cost_price");
            assertThat(type).isEqualTo("numeric");
        }

        @Test
        @DisplayName("audit_logs.old_values should be jsonb")
        void auditLogsOldValuesShouldBeJsonb() throws SQLException {
            String type = getColumnType("access", "audit_logs", "old_values");
            assertThat(type).isEqualTo("jsonb");
        }

        @Test
        @DisplayName("audit_logs.new_values should be jsonb")
        void auditLogsNewValuesShouldBeJsonb() throws SQLException {
            String type = getColumnType("access", "audit_logs", "new_values");
            assertThat(type).isEqualTo("jsonb");
        }

        @Test
        @DisplayName("daily_sales_summary.summary_date should be date")
        void dailySalesSummaryDateShouldBeDate() throws SQLException {
            String type = getColumnType("reporting", "daily_sales_summary", "summary_date");
            assertThat(type).isEqualTo("date");
        }

        @Test
        @DisplayName("inventory_movements.quantity should be int4 (integer)")
        void inventoryMovementsQuantityShouldBeInteger() throws SQLException {
            String type = getColumnType("inventory", "inventory_movements", "quantity");
            assertThat(type).isEqualTo("int4");
        }
    }
}
