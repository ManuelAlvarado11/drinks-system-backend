-- ============================================================================
-- V5__seed_roles_and_permissions.sql
-- Complete RBAC setup: roles, all permissions, and role-permission mappings.
-- Every role gets the correct permissions from the start.
-- ============================================================================

SET search_path TO access;

-- =============================================================================
-- ROLES
-- =============================================================================
INSERT INTO access.roles (code, name, description) VALUES
('ADMINISTRADOR_SISTEMA', 'Administrador del Sistema', 'Acceso total a todas las funcionalidades del sistema'),
('GERENTE_SUCURSAL', 'Gerente de Sucursal', 'Gestión completa de operaciones diarias de una sucursal'),
('CAJERO', 'Cajero', 'Operaciones de POS, caja, cuentas y ventas');

-- =============================================================================
-- PERMISSIONS — Organized by module
-- =============================================================================

-- ─── Módulo: MODULOS (acceso a módulos principales del sistema) ──────────────
INSERT INTO access.permissions (code, name, module) VALUES
('MODULE_ACCESS', 'Acceso al módulo de administración', 'MODULOS'),
('MODULE_SALES', 'Acceso al módulo de ventas', 'MODULOS'),
('MODULE_INVENTORY', 'Acceso al módulo de inventario', 'MODULOS'),
('MODULE_REPORTING', 'Acceso al módulo de reportes', 'MODULOS');

-- ─── Módulo: DASHBOARD ───────────────────────────────────────────────────────
INSERT INTO access.permissions (code, name, module) VALUES
('DASHBOARD_VIEW', 'Ver dashboard', 'DASHBOARD');

-- ─── Módulo: ADMINISTRACION (gestión de roles, permisos, menú) ───────────────
INSERT INTO access.permissions (code, name, module) VALUES
('ROLES_CREATE', 'Crear roles', 'ADMINISTRACION'),
('ROLES_READ', 'Ver roles', 'ADMINISTRACION'),
('ROLES_UPDATE', 'Editar roles', 'ADMINISTRACION'),
('ROLES_DELETE', 'Eliminar roles', 'ADMINISTRACION'),
('PERMISSIONS_READ', 'Ver permisos', 'ADMINISTRACION'),
('PERMISSIONS_UPDATE', 'Editar permisos', 'ADMINISTRACION'),
('MENU_OPTIONS_READ', 'Ver opciones de menú', 'ADMINISTRACION'),
('MENU_OPTIONS_UPDATE', 'Editar opciones de menú', 'ADMINISTRACION');

-- ─── Módulo: USUARIOS ────────────────────────────────────────────────────────
INSERT INTO access.permissions (code, name, module) VALUES
('USERS_CREATE', 'Crear usuarios', 'USUARIOS'),
('USERS_READ', 'Ver usuarios', 'USUARIOS'),
('USERS_UPDATE', 'Editar usuarios', 'USUARIOS'),
('USERS_DELETE', 'Eliminar usuarios', 'USUARIOS');

-- ─── Módulo: SUCURSALES ──────────────────────────────────────────────────────
INSERT INTO access.permissions (code, name, module) VALUES
('BRANCHES_CREATE', 'Crear sucursales', 'SUCURSALES'),
('BRANCHES_READ', 'Ver sucursales', 'SUCURSALES'),
('BRANCHES_UPDATE', 'Editar sucursales', 'SUCURSALES');

-- ─── Módulo: INVENTARIO ──────────────────────────────────────────────────────
INSERT INTO access.permissions (code, name, module) VALUES
('INVENTORY_READ', 'Ver inventario', 'INVENTARIO'),
('INVENTORY_MOVEMENTS', 'Registrar movimientos', 'INVENTARIO'),
('INVENTORY_PRODUCTS', 'Gestión de productos', 'INVENTARIO'),
('INVENTORY_CATEGORIES', 'Gestión de categorías', 'INVENTARIO'),
('INVENTORY_STOCK', 'Consultar existencias', 'INVENTARIO'),
('INVENTORY_SUPPLIERS', 'Gestión de proveedores', 'INVENTARIO'),
('PRODUCTS_CREATE', 'Crear productos', 'INVENTARIO'),
('PRODUCTS_UPDATE', 'Editar productos', 'INVENTARIO');

-- ─── Módulo: COMPRAS ─────────────────────────────────────────────────────────
INSERT INTO access.permissions (code, name, module) VALUES
('PURCHASES_CREATE', 'Crear órdenes de compra', 'COMPRAS'),
('PURCHASES_READ', 'Ver órdenes de compra', 'COMPRAS'),
('PURCHASES_RECEIVE', 'Recibir compras', 'COMPRAS'),
('INVENTORY_PURCHASES', 'Gestión completa de compras', 'COMPRAS');

-- ─── Módulo: VENTAS ──────────────────────────────────────────────────────────
INSERT INTO access.permissions (code, name, module) VALUES
('SALES_CREATE', 'Registrar ventas', 'VENTAS'),
('SALES_READ', 'Ver ventas', 'VENTAS'),
('SALES_CANCEL', 'Anular ventas', 'VENTAS'),
('SALES_CUSTOMERS', 'Gestión de clientes', 'VENTAS'),
('ACCOUNTS_READ', 'Ver cuentas', 'VENTAS'),
('ACCOUNTS_CREATE', 'Crear cuentas', 'VENTAS'),
('ACCOUNTS_UPDATE', 'Editar cuentas', 'VENTAS'),
('ACCOUNTS_ADD_ITEMS', 'Agregar productos a cuenta', 'VENTAS'),
('ACCOUNTS_CANCEL', 'Cancelar cuenta', 'VENTAS'),
('ACCOUNTS_CANCEL_ITEMS', 'Cancelar items de cuenta', 'VENTAS');

-- ─── Módulo: CAJA ────────────────────────────────────────────────────────────
INSERT INTO access.permissions (code, name, module) VALUES
('CASH_OPEN', 'Abrir caja', 'CAJA'),
('CASH_CLOSE', 'Cerrar caja', 'CAJA'),
('CASH_MOVEMENTS', 'Registrar movimientos de caja', 'CAJA'),
('CASH_REGISTERS_READ', 'Ver cajas registradoras', 'CAJA'),
('CASH_REGISTERS_CREATE', 'Crear cajas registradoras', 'CAJA'),
('CASH_REGISTERS_UPDATE', 'Editar cajas registradoras', 'CAJA'),
('CASH_REGISTERS_OPEN', 'Abrir caja registradora', 'CAJA'),
('CASH_REGISTERS_CLOSE', 'Cerrar caja registradora', 'CAJA'),
('CASH_REGISTERS_MOVEMENTS', 'Registrar movimientos en caja registradora', 'CAJA');

-- ─── Módulo: REPORTES ────────────────────────────────────────────────────────
INSERT INTO access.permissions (code, name, module) VALUES
('REPORTS_VIEW', 'Ver reportes', 'REPORTES'),
('REPORTS_EXPORT', 'Exportar reportes', 'REPORTES'),
('REPORTING_READ', 'Ver reportes del sistema', 'REPORTES');

-- ─── Módulo: CONFIGURACION ───────────────────────────────────────────────────
INSERT INTO access.permissions (code, name, module) VALUES
('CONFIG_PARAMS', 'Gestionar parámetros', 'CONFIGURACION'),
('CONFIG_CATALOGS', 'Gestionar catálogos', 'CONFIGURACION');

-- =============================================================================
-- ROLE-PERMISSION ASSIGNMENTS
-- =============================================================================

-- ─── ADMINISTRADOR_SISTEMA: TODOS los permisos ───────────────────────────────
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM access.roles r
CROSS JOIN access.permissions p
WHERE r.code = 'ADMINISTRADOR_SISTEMA';

-- ─── GERENTE_SUCURSAL: Todo excepto CONFIGURACION y ADMINISTRACION ───────────
-- Accede a: MODULOS, DASHBOARD, USUARIOS (lectura), SUCURSALES (lectura),
--           INVENTARIO, COMPRAS, VENTAS, CAJA, REPORTES
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM access.roles r
CROSS JOIN access.permissions p
WHERE r.code = 'GERENTE_SUCURSAL'
  AND p.code IN (
    -- Módulos
    'MODULE_SALES', 'MODULE_INVENTORY', 'MODULE_REPORTING',
    -- Dashboard
    'DASHBOARD_VIEW',
    -- Usuarios (solo lectura)
    'USERS_READ',
    -- Sucursales (solo lectura)
    'BRANCHES_READ',
    -- Inventario completo
    'INVENTORY_READ', 'INVENTORY_MOVEMENTS', 'INVENTORY_PRODUCTS',
    'INVENTORY_CATEGORIES', 'INVENTORY_STOCK', 'INVENTORY_SUPPLIERS',
    'PRODUCTS_CREATE', 'PRODUCTS_UPDATE',
    -- Compras
    'PURCHASES_CREATE', 'PURCHASES_READ', 'PURCHASES_RECEIVE', 'INVENTORY_PURCHASES',
    -- Ventas completo
    'SALES_CREATE', 'SALES_READ', 'SALES_CANCEL', 'SALES_CUSTOMERS',
    'ACCOUNTS_READ', 'ACCOUNTS_CREATE', 'ACCOUNTS_UPDATE',
    'ACCOUNTS_ADD_ITEMS', 'ACCOUNTS_CANCEL', 'ACCOUNTS_CANCEL_ITEMS',
    -- Caja completo
    'CASH_OPEN', 'CASH_CLOSE', 'CASH_MOVEMENTS',
    'CASH_REGISTERS_READ', 'CASH_REGISTERS_CREATE', 'CASH_REGISTERS_UPDATE',
    'CASH_REGISTERS_OPEN', 'CASH_REGISTERS_CLOSE', 'CASH_REGISTERS_MOVEMENTS',
    -- Reportes
    'REPORTS_VIEW', 'REPORTS_EXPORT', 'REPORTING_READ'
  );

-- ─── CAJERO: Operaciones de POS, caja y consultas básicas ────────────────────
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM access.roles r
CROSS JOIN access.permissions p
WHERE r.code = 'CAJERO'
  AND p.code IN (
    -- Módulos (ventas + inventario para consulta de productos)
    'MODULE_SALES', 'MODULE_INVENTORY',
    -- Dashboard
    'DASHBOARD_VIEW',
    -- Sucursales (lectura, necesario para contexto de sucursal)
    'BRANCHES_READ',
    -- Ventas operativas
    'SALES_CREATE', 'SALES_READ', 'SALES_CUSTOMERS',
    'ACCOUNTS_READ', 'ACCOUNTS_CREATE', 'ACCOUNTS_UPDATE', 'ACCOUNTS_ADD_ITEMS',
    -- Caja operativa
    'CASH_OPEN', 'CASH_CLOSE', 'CASH_MOVEMENTS',
    'CASH_REGISTERS_READ', 'CASH_REGISTERS_OPEN', 'CASH_REGISTERS_CLOSE',
    'CASH_REGISTERS_MOVEMENTS',
    -- Inventario consulta (productos y categorías para POS, stock)
    'INVENTORY_READ', 'INVENTORY_STOCK', 'INVENTORY_PRODUCTS', 'INVENTORY_CATEGORIES'
  );
