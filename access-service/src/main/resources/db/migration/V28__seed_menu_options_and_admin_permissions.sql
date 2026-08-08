-- V28__seed_menu_options_and_admin_permissions.sql
-- Crea permisos faltantes (módulos, roles, permisos, menú), pobla system_menu_options
-- y asigna todos los permisos nuevos al rol ADMINISTRADOR_SISTEMA.

SET search_path TO access;

-- =============================================================================
-- 1. Permisos nuevos que no existían en V23
-- =============================================================================

-- Permisos de módulo (usados por permissionGuard en rutas principales)
INSERT INTO access.permissions (code, name, module) VALUES
('MODULE_ACCESS', 'Acceso al módulo de administración', 'MODULOS'),
('MODULE_SALES', 'Acceso al módulo de ventas', 'MODULOS'),
('MODULE_INVENTORY', 'Acceso al módulo de inventario', 'MODULOS'),
('MODULE_REPORTING', 'Acceso al módulo de reportes', 'MODULOS');

-- Permisos CRUD para Roles
INSERT INTO access.permissions (code, name, module) VALUES
('ROLES_CREATE', 'Crear roles', 'ADMINISTRACION'),
('ROLES_READ', 'Ver roles', 'ADMINISTRACION'),
('ROLES_UPDATE', 'Editar roles', 'ADMINISTRACION'),
('ROLES_DELETE', 'Eliminar roles', 'ADMINISTRACION');

-- Permisos para gestión de permisos
INSERT INTO access.permissions (code, name, module) VALUES
('PERMISSIONS_READ', 'Ver permisos', 'ADMINISTRACION'),
('PERMISSIONS_UPDATE', 'Editar permisos', 'ADMINISTRACION');

-- Permisos para opciones de menú
INSERT INTO access.permissions (code, name, module) VALUES
('MENU_OPTIONS_READ', 'Ver opciones de menú', 'ADMINISTRACION'),
('MENU_OPTIONS_UPDATE', 'Editar opciones de menú', 'ADMINISTRACION');

-- Permiso de Dashboard (acceso general)
INSERT INTO access.permissions (code, name, module) VALUES
('DASHBOARD_VIEW', 'Ver dashboard', 'DASHBOARD');

-- =============================================================================
-- 2. Asignar todos los permisos nuevos al rol ADMINISTRADOR_SISTEMA
-- =============================================================================
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM access.roles r
CROSS JOIN access.permissions p
WHERE r.code = 'ADMINISTRADOR_SISTEMA'
  AND p.code IN (
    'MODULE_ACCESS', 'MODULE_SALES', 'MODULE_INVENTORY', 'MODULE_REPORTING',
    'ROLES_CREATE', 'ROLES_READ', 'ROLES_UPDATE', 'ROLES_DELETE',
    'PERMISSIONS_READ', 'PERMISSIONS_UPDATE',
    'MENU_OPTIONS_READ', 'MENU_OPTIONS_UPDATE',
    'DASHBOARD_VIEW'
  )
  AND NOT EXISTS (
    SELECT 1 FROM access.role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- =============================================================================
-- 3. Poblar system_menu_options (estructura jerárquica del menú)
--    Nivel 1: Grupos (parent_id = NULL)
--    Nivel 2: Items con ruta (parent_id = grupo padre)
-- =============================================================================

-- ─── Grupo: Dashboard ────────────────────────────────────────────────────────
INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (1, 'Dashboard', NULL, 'dashboard', NULL, NULL, 1);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (2, 'Inicio', '/dashboard', 'home', 1,
  (SELECT id FROM access.permissions WHERE code = 'DASHBOARD_VIEW'), 1);

-- ─── Grupo: Ventas ───────────────────────────────────────────────────────────
INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (3, 'Ventas', NULL, 'point_of_sale', NULL,
  (SELECT id FROM access.permissions WHERE code = 'MODULE_SALES'), 2);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (4, 'Cuentas abiertas', '/sales/open-tabs', 'receipt_long', 3,
  (SELECT id FROM access.permissions WHERE code = 'SALES_READ'), 1);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (5, 'Nueva venta', '/sales/new', 'add_shopping_cart', 3,
  (SELECT id FROM access.permissions WHERE code = 'SALES_CREATE'), 2);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (6, 'Historial', '/sales/history', 'history', 3,
  (SELECT id FROM access.permissions WHERE code = 'SALES_READ'), 3);

-- ─── Grupo: Inventario ───────────────────────────────────────────────────────
INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (7, 'Inventario', NULL, 'inventory_2', NULL,
  (SELECT id FROM access.permissions WHERE code = 'MODULE_INVENTORY'), 3);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (8, 'Productos', '/inventory/products', 'liquor', 7,
  (SELECT id FROM access.permissions WHERE code = 'PRODUCTS_CREATE'), 1);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (9, 'Existencias', '/inventory/stock', 'shelves', 7,
  (SELECT id FROM access.permissions WHERE code = 'INVENTORY_READ'), 2);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (10, 'Movimientos', '/inventory/movements', 'swap_horiz', 7,
  (SELECT id FROM access.permissions WHERE code = 'INVENTORY_MOVEMENTS'), 3);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (11, 'Ajustes', '/inventory/adjustments', 'tune', 7,
  (SELECT id FROM access.permissions WHERE code = 'INVENTORY_MOVEMENTS'), 4);

-- ─── Grupo: Reportes ─────────────────────────────────────────────────────────
INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (12, 'Reportes', NULL, 'assessment', NULL,
  (SELECT id FROM access.permissions WHERE code = 'MODULE_REPORTING'), 4);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (13, 'Ventas', '/reporting/sales', 'trending_up', 12,
  (SELECT id FROM access.permissions WHERE code = 'REPORTS_VIEW'), 1);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (14, 'Inventario', '/reporting/inventory', 'analytics', 12,
  (SELECT id FROM access.permissions WHERE code = 'REPORTS_VIEW'), 2);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (15, 'Dashboard', '/reporting/dashboard', 'pie_chart', 12,
  (SELECT id FROM access.permissions WHERE code = 'REPORTS_VIEW'), 3);

-- ─── Grupo: Administración ───────────────────────────────────────────────────
INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (16, 'Administración', NULL, 'admin_panel_settings', NULL,
  (SELECT id FROM access.permissions WHERE code = 'MODULE_ACCESS'), 5);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (17, 'Sucursales', '/administration/branches', 'store', 16,
  (SELECT id FROM access.permissions WHERE code = 'BRANCHES_READ'), 1);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (18, 'Usuarios', '/administration/users', 'people', 16,
  (SELECT id FROM access.permissions WHERE code = 'USERS_READ'), 2);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (19, 'Roles', '/administration/roles', 'badge', 16,
  (SELECT id FROM access.permissions WHERE code = 'ROLES_READ'), 3);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (20, 'Permisos', '/administration/permissions', 'lock', 16,
  (SELECT id FROM access.permissions WHERE code = 'PERMISSIONS_READ'), 4);

INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (21, 'Opciones del Menú', '/administration/menu-options', 'menu_book', 16,
  (SELECT id FROM access.permissions WHERE code = 'MENU_OPTIONS_READ'), 5);

-- Resetear la secuencia del ID para evitar conflictos futuros
SELECT setval('access.system_menu_options_id_seq', (SELECT MAX(id) FROM access.system_menu_options));
