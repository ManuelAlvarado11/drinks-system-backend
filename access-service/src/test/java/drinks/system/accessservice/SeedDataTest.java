package drinks.system.accessservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de validación de seed data.
 * Verifica que los datos iniciales insertados por las migraciones V23-V27 existen correctamente.
 *
 * Validates: Requirements 18.1, 18.2, 18.3, 18.4, 18.5
 */
@DisplayName("Seed Data Tests")
class SeedDataTest extends AbstractDatabaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // =========================================================================
    // Roles (Req 18.1)
    // =========================================================================

    @Nested
    @DisplayName("Roles - Req 18.1")
    class RolesTests {

        @Test
        @DisplayName("Should have role ADMINISTRADOR_SISTEMA")
        void shouldHaveAdminRole() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.roles WHERE code = 'ADMINISTRADOR_SISTEMA'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should have role GERENTE_SUCURSAL")
        void shouldHaveManagerRole() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.roles WHERE code = 'GERENTE_SUCURSAL'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should have role CAJERO")
        void shouldHaveCashierRole() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.roles WHERE code = 'CAJERO'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should have exactly 3 initial roles")
        void shouldHaveExactlyThreeRoles() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.roles",
                    Integer.class);
            assertThat(count).isEqualTo(3);
        }
    }

    // =========================================================================
    // Permissions (Req 18.1)
    // =========================================================================

    @Nested
    @DisplayName("Permissions - Req 18.1")
    class PermissionsTests {

        @Test
        @DisplayName("Should have permissions count > 0")
        void shouldHavePermissions() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.permissions",
                    Integer.class);
            assertThat(count).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should have permission USERS_CREATE")
        void shouldHaveUsersCreatePermission() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.permissions WHERE code = 'USERS_CREATE'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should have permission SALES_CREATE")
        void shouldHaveSalesCreatePermission() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.permissions WHERE code = 'SALES_CREATE'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should have permission CASH_OPEN")
        void shouldHaveCashOpenPermission() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.permissions WHERE code = 'CASH_OPEN'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should have permission INVENTORY_READ")
        void shouldHaveInventoryReadPermission() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.permissions WHERE code = 'INVENTORY_READ'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should have permissions across multiple modules")
        void shouldHavePermissionsAcrossModules() {
            List<Map<String, Object>> modules = jdbcTemplate.queryForList(
                    "SELECT DISTINCT module FROM access.permissions ORDER BY module");
            assertThat(modules).hasSizeGreaterThanOrEqualTo(7);
        }
    }

    // =========================================================================
    // Role-Permission Assignments (Req 18.1)
    // =========================================================================

    @Nested
    @DisplayName("Role-Permission Assignments - Req 18.1")
    class RolePermissionTests {

        @Test
        @DisplayName("ADMINISTRADOR_SISTEMA should have all permissions")
        void adminShouldHaveAllPermissions() {
            Integer totalPermissions = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.permissions",
                    Integer.class);
            Integer adminPermissions = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.role_permissions rp " +
                    "JOIN access.roles r ON r.id = rp.role_id " +
                    "WHERE r.code = 'ADMINISTRADOR_SISTEMA'",
                    Integer.class);
            assertThat(adminPermissions).isEqualTo(totalPermissions);
        }

        @Test
        @DisplayName("CAJERO should have limited permissions (6 specific ones)")
        void cashierShouldHaveLimitedPermissions() {
            Integer cajeroPermissions = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.role_permissions rp " +
                    "JOIN access.roles r ON r.id = rp.role_id " +
                    "WHERE r.code = 'CAJERO'",
                    Integer.class);
            assertThat(cajeroPermissions).isEqualTo(6);
        }

        @Test
        @DisplayName("CAJERO should have SALES_CREATE permission")
        void cashierShouldHaveSalesCreate() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.role_permissions rp " +
                    "JOIN access.roles r ON r.id = rp.role_id " +
                    "JOIN access.permissions p ON p.id = rp.permission_id " +
                    "WHERE r.code = 'CAJERO' AND p.code = 'SALES_CREATE'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("GERENTE_SUCURSAL should have all permissions except CONFIGURACION module")
        void managerShouldHaveAllExceptConfig() {
            Integer nonConfigPermissions = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.permissions WHERE module != 'CONFIGURACION'",
                    Integer.class);
            Integer managerPermissions = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.role_permissions rp " +
                    "JOIN access.roles r ON r.id = rp.role_id " +
                    "WHERE r.code = 'GERENTE_SUCURSAL'",
                    Integer.class);
            assertThat(managerPermissions).isEqualTo(nonConfigPermissions);
        }
    }

    // =========================================================================
    // Admin User (Req 18.5)
    // =========================================================================

    @Nested
    @DisplayName("Admin User - Req 18.5")
    class AdminUserTests {

        @Test
        @DisplayName("Should have user 'admin'")
        void shouldHaveAdminUser() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.users WHERE username = 'admin'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Admin user should be active")
        void adminShouldBeActive() {
            Boolean isActive = jdbcTemplate.queryForObject(
                    "SELECT is_active FROM access.users WHERE username = 'admin'",
                    Boolean.class);
            assertThat(isActive).isTrue();
        }

        @Test
        @DisplayName("Admin user should have role ADMINISTRADOR_SISTEMA")
        void adminShouldHaveAdminRole() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.user_roles ur " +
                    "JOIN access.users u ON u.id = ur.user_id " +
                    "JOIN access.roles r ON r.id = ur.role_id " +
                    "WHERE u.username = 'admin' AND r.code = 'ADMINISTRADOR_SISTEMA'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }
    }

    // =========================================================================
    // System Parameters (Req 18.3)
    // =========================================================================

    @Nested
    @DisplayName("System Parameters - Req 18.3")
    class SystemParametersTests {

        @Test
        @DisplayName("Should have TAX_RATE parameter")
        void shouldHaveTaxRate() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.system_parameters WHERE parameter_key = 'TAX_RATE'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should have DEFAULT_CURRENCY parameter")
        void shouldHaveDefaultCurrency() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.system_parameters WHERE parameter_key = 'DEFAULT_CURRENCY'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should have LOW_STOCK_THRESHOLD parameter")
        void shouldHaveLowStockThreshold() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.system_parameters WHERE parameter_key = 'LOW_STOCK_THRESHOLD'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should have SESSION_TIMEOUT_MINUTES parameter")
        void shouldHaveSessionTimeout() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.system_parameters WHERE parameter_key = 'SESSION_TIMEOUT_MINUTES'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should have at least 8 system parameters")
        void shouldHaveMinimumParameters() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.system_parameters",
                    Integer.class);
            assertThat(count).isGreaterThanOrEqualTo(8);
        }
    }

    // =========================================================================
    // Catalogs (Req 18.4)
    // =========================================================================

    @Nested
    @DisplayName("Catalogs - Req 18.4")
    class CatalogsTests {

        @Test
        @DisplayName("Should have PAYMENT_METHOD catalogs")
        void shouldHavePaymentMethodCatalogs() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.catalogs WHERE catalog_type = 'PAYMENT_METHOD'",
                    Integer.class);
            assertThat(count).isGreaterThanOrEqualTo(5);
        }

        @Test
        @DisplayName("Should have CASH payment method")
        void shouldHaveCashPaymentMethod() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.catalogs WHERE catalog_type = 'PAYMENT_METHOD' AND code = 'CASH'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should have CARD payment method")
        void shouldHaveCardPaymentMethod() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.catalogs WHERE catalog_type = 'PAYMENT_METHOD' AND code = 'CARD'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Should have MOVEMENT_TYPE catalogs")
        void shouldHaveMovementTypeCatalogs() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.catalogs WHERE catalog_type = 'MOVEMENT_TYPE'",
                    Integer.class);
            assertThat(count).isGreaterThanOrEqualTo(6);
        }

        @Test
        @DisplayName("Should have NOTIFICATION_TYPE catalogs")
        void shouldHaveNotificationTypeCatalogs() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.catalogs WHERE catalog_type = 'NOTIFICATION_TYPE'",
                    Integer.class);
            assertThat(count).isGreaterThanOrEqualTo(4);
        }

        @Test
        @DisplayName("Should have ACCOUNT_STATUS catalogs")
        void shouldHaveAccountStatusCatalogs() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.catalogs WHERE catalog_type = 'ACCOUNT_STATUS'",
                    Integer.class);
            assertThat(count).isGreaterThanOrEqualTo(3);
        }

        @Test
        @DisplayName("Should have PURCHASE_STATUS catalogs")
        void shouldHavePurchaseStatusCatalogs() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM access.catalogs WHERE catalog_type = 'PURCHASE_STATUS'",
                    Integer.class);
            assertThat(count).isGreaterThanOrEqualTo(4);
        }
    }

    // =========================================================================
    // Default Customer (Req 18.2)
    // =========================================================================

    @Nested
    @DisplayName("Default Customer - Req 18.2")
    class DefaultCustomerTests {

        @Test
        @DisplayName("Should have 'Consumidor Final' customer with id=1")
        void shouldHaveConsumidorFinalWithId1() {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM sales.customers WHERE id = 1 AND first_name = 'Consumidor' AND last_name = 'Final'",
                    Integer.class);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("Default customer should be active")
        void defaultCustomerShouldBeActive() {
            Boolean isActive = jdbcTemplate.queryForObject(
                    "SELECT is_active FROM sales.customers WHERE id = 1",
                    Boolean.class);
            assertThat(isActive).isTrue();
        }
    }
}
