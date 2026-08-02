-- V23__seed_roles_permissions.sql
-- Seed de roles iniciales, permisos por módulo, y asignación de permisos a roles

SET search_path TO access;

-- =============================================================================
-- Roles iniciales
-- =============================================================================
INSERT INTO access.roles (code, name, description) VALUES
('ADMINISTRADOR_SISTEMA', 'Administrador del Sistema', 'Acceso total a todas las funcionalidades'),
('GERENTE_SUCURSAL', 'Gerente de Sucursal', 'Gestión completa de una sucursal específica'),
('CAJERO', 'Cajero', 'Operaciones de caja, cuentas y ventas');

-- =============================================================================
-- Permisos por módulo
-- =============================================================================

-- Módulo: USUARIOS
INSERT INTO access.permissions (code, name, module) VALUES
('USERS_CREATE', 'Crear usuarios', 'USUARIOS'),
('USERS_READ', 'Ver usuarios', 'USUARIOS'),
('USERS_UPDATE', 'Editar usuarios', 'USUARIOS'),
('USERS_DELETE', 'Eliminar usuarios', 'USUARIOS');

-- Módulo: SUCURSALES
INSERT INTO access.permissions (code, name, module) VALUES
('BRANCHES_CREATE', 'Crear sucursales', 'SUCURSALES'),
('BRANCHES_READ', 'Ver sucursales', 'SUCURSALES'),
('BRANCHES_UPDATE', 'Editar sucursales', 'SUCURSALES');

-- Módulo: VENTAS
INSERT INTO access.permissions (code, name, module) VALUES
('SALES_CREATE', 'Registrar ventas', 'VENTAS'),
('SALES_READ', 'Ver ventas', 'VENTAS'),
('SALES_CANCEL', 'Anular ventas', 'VENTAS');

-- Módulo: CAJA
INSERT INTO access.permissions (code, name, module) VALUES
('CASH_OPEN', 'Abrir caja', 'CAJA'),
('CASH_CLOSE', 'Cerrar caja', 'CAJA'),
('CASH_MOVEMENTS', 'Registrar movimientos de caja', 'CAJA');

-- Módulo: INVENTARIO
INSERT INTO access.permissions (code, name, module) VALUES
('INVENTORY_READ', 'Ver inventario', 'INVENTARIO'),
('INVENTORY_MOVEMENTS', 'Registrar movimientos', 'INVENTARIO'),
('PRODUCTS_CREATE', 'Crear productos', 'INVENTARIO'),
('PRODUCTS_UPDATE', 'Editar productos', 'INVENTARIO');

-- Módulo: COMPRAS
INSERT INTO access.permissions (code, name, module) VALUES
('PURCHASES_CREATE', 'Crear órdenes de compra', 'COMPRAS'),
('PURCHASES_READ', 'Ver órdenes de compra', 'COMPRAS'),
('PURCHASES_RECEIVE', 'Recibir compras', 'COMPRAS');

-- Módulo: REPORTES
INSERT INTO access.permissions (code, name, module) VALUES
('REPORTS_VIEW', 'Ver reportes', 'REPORTES'),
('REPORTS_EXPORT', 'Exportar reportes', 'REPORTES');

-- Módulo: CONFIGURACIÓN
INSERT INTO access.permissions (code, name, module) VALUES
('CONFIG_PARAMS', 'Gestionar parámetros', 'CONFIGURACION'),
('CONFIG_CATALOGS', 'Gestionar catálogos', 'CONFIGURACION');

-- =============================================================================
-- Asignación de permisos a roles
-- =============================================================================

-- ADMINISTRADOR_SISTEMA: todos los permisos
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM access.roles r, access.permissions p WHERE r.code = 'ADMINISTRADOR_SISTEMA';

-- GERENTE_SUCURSAL: todos menos configuración del sistema
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM access.roles r, access.permissions p
WHERE r.code = 'GERENTE_SUCURSAL' AND p.module NOT IN ('CONFIGURACION');

-- CAJERO: ventas, caja, inventario (solo lectura)
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM access.roles r, access.permissions p
WHERE r.code = 'CAJERO' AND p.code IN (
    'SALES_CREATE', 'SALES_READ', 'CASH_OPEN', 'CASH_CLOSE',
    'CASH_MOVEMENTS', 'INVENTORY_READ'
);
