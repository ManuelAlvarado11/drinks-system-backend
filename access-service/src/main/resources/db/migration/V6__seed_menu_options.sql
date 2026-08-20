-- ============================================================================
-- V6__seed_menu_options.sql
-- Complete hierarchical menu structure with permission links.
-- Level 1 = Groups (parent_id = NULL), Level 2 = Items (parent_id = group)
-- Each item links to the permission that controls its visibility.
-- ============================================================================

SET search_path TO access;

-- =============================================================================
-- GRUPO: Dashboard (visible para todos los autenticados)
-- =============================================================================
INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (1, 'Dashboard', NULL, 'dashboard', NULL, NULL, 1);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (2, 'Inicio', '/dashboard', 'home', 1,
  (SELECT id FROM access.permissions WHERE code = 'DASHBOARD_VIEW'), 1);

-- =============================================================================
-- GRUPO: Ventas
-- =============================================================================
INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (3, 'Ventas', NULL, 'point_of_sale', NULL,
  (SELECT id FROM access.permissions WHERE code = 'MODULE_SALES'), 2);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (4, 'POS', '/sales/pos', 'point_of_sale', 3,
  (SELECT id FROM access.permissions WHERE code = 'ACCOUNTS_CREATE'), 1);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (5, 'Cuentas', '/sales/accounts', 'receipt_long', 3,
  (SELECT id FROM access.permissions WHERE code = 'ACCOUNTS_READ'), 2);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (6, 'Historial de Ventas', '/sales/history', 'history', 3,
  (SELECT id FROM access.permissions WHERE code = 'SALES_READ'), 3);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (7, 'Clientes', '/sales/customers', 'people', 3,
  (SELECT id FROM access.permissions WHERE code = 'SALES_CUSTOMERS'), 4);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (8, 'Cajas Registradoras', '/sales/cash-registers', 'payments', 3,
  (SELECT id FROM access.permissions WHERE code = 'CASH_REGISTERS_READ'), 5);

-- =============================================================================
-- GRUPO: Inventario
-- =============================================================================
INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (9, 'Inventario', NULL, 'inventory_2', NULL,
  (SELECT id FROM access.permissions WHERE code = 'MODULE_INVENTORY'), 3);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (10, 'Productos', '/inventory/products', 'liquor', 9,
  (SELECT id FROM access.permissions WHERE code = 'INVENTORY_PRODUCTS'), 1);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (11, 'Categorías', '/inventory/categories', 'category', 9,
  (SELECT id FROM access.permissions WHERE code = 'INVENTORY_CATEGORIES'), 2);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (12, 'Existencias', '/inventory/stock', 'shelves', 9,
  (SELECT id FROM access.permissions WHERE code = 'INVENTORY_STOCK'), 3);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (13, 'Movimientos', '/inventory/movements', 'swap_horiz', 9,
  (SELECT id FROM access.permissions WHERE code = 'INVENTORY_MOVEMENTS'), 4);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (14, 'Proveedores', '/inventory/suppliers', 'local_shipping', 9,
  (SELECT id FROM access.permissions WHERE code = 'INVENTORY_SUPPLIERS'), 5);

-- =============================================================================
-- GRUPO: Reportes
-- =============================================================================
INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (15, 'Reportes', NULL, 'assessment', NULL,
  (SELECT id FROM access.permissions WHERE code = 'MODULE_REPORTING'), 4);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (16, 'Ventas Diarias', '/reporting/daily-sales', 'today', 15,
  (SELECT id FROM access.permissions WHERE code = 'REPORTING_READ'), 1);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (17, 'Ventas Mensuales', '/reporting/monthly-sales', 'calendar_month', 15,
  (SELECT id FROM access.permissions WHERE code = 'REPORTING_READ'), 2);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (18, 'Ranking de Productos', '/reporting/product-ranking', 'leaderboard', 15,
  (SELECT id FROM access.permissions WHERE code = 'REPORTING_READ'), 3);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (19, 'Estado de Inventario', '/reporting/inventory-status', 'inventory', 15,
  (SELECT id FROM access.permissions WHERE code = 'REPORTING_READ'), 4);

-- =============================================================================
-- GRUPO: Administración
-- =============================================================================
INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (20, 'Administración', NULL, 'admin_panel_settings', NULL,
  (SELECT id FROM access.permissions WHERE code = 'MODULE_ACCESS'), 5);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (21, 'Sucursales', '/administration/branches', 'store', 20,
  (SELECT id FROM access.permissions WHERE code = 'BRANCHES_READ'), 1);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (22, 'Usuarios', '/administration/users', 'people', 20,
  (SELECT id FROM access.permissions WHERE code = 'USERS_READ'), 2);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (23, 'Roles', '/administration/roles', 'badge', 20,
  (SELECT id FROM access.permissions WHERE code = 'ROLES_READ'), 3);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (24, 'Permisos', '/administration/permissions', 'lock', 20,
  (SELECT id FROM access.permissions WHERE code = 'PERMISSIONS_READ'), 4);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (25, 'Opciones del Menú', '/administration/menu-options', 'menu_book', 20,
  (SELECT id FROM access.permissions WHERE code = 'MENU_OPTIONS_READ'), 5);

-- =============================================================================
-- Resetear la secuencia para evitar conflictos en inserts futuros
-- =============================================================================
SELECT setval('access.system_menu_options_id_seq', (SELECT MAX(id) FROM access.system_menu_options));
