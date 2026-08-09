-- V30__sales_permissions_and_menu_options.sql
-- Crea permisos faltantes del módulo de ventas, actualiza/agrega opciones de menú
-- y asigna los permisos al ADMINISTRADOR_SISTEMA y al CAJERO.

SET search_path TO access;

-- =============================================================================
-- 1. Permisos nuevos del módulo de ventas (no existían previamente)
-- =============================================================================
INSERT INTO access.permissions (code, name, module) VALUES
('ACCOUNTS_READ', 'Ver cuentas', 'VENTAS'),
('ACCOUNTS_CREATE', 'Crear cuentas', 'VENTAS'),
('ACCOUNTS_UPDATE', 'Editar cuentas', 'VENTAS'),
('SALES_CUSTOMERS', 'Gestión de clientes', 'VENTAS'),
('CASH_REGISTERS_READ', 'Ver cajas registradoras', 'CAJA'),
('CASH_REGISTERS_CREATE', 'Crear cajas registradoras', 'CAJA'),
('CASH_REGISTERS_UPDATE', 'Editar cajas registradoras', 'CAJA');

-- =============================================================================
-- 2. Asignar TODOS los permisos nuevos al rol ADMINISTRADOR_SISTEMA
-- =============================================================================
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM access.roles r
CROSS JOIN access.permissions p
WHERE r.code = 'ADMINISTRADOR_SISTEMA'
  AND p.code IN (
    'ACCOUNTS_READ', 'ACCOUNTS_CREATE', 'ACCOUNTS_UPDATE',
    'SALES_CUSTOMERS',
    'CASH_REGISTERS_READ', 'CASH_REGISTERS_CREATE', 'CASH_REGISTERS_UPDATE'
  )
  AND NOT EXISTS (
    SELECT 1 FROM access.role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- =============================================================================
-- 3. Asignar permisos operativos al rol CAJERO
--    (lectura de cuentas, crear/editar cuentas, ver cajas)
-- =============================================================================
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM access.roles r
CROSS JOIN access.permissions p
WHERE r.code = 'CAJERO'
  AND p.code IN (
    'ACCOUNTS_READ', 'ACCOUNTS_CREATE', 'ACCOUNTS_UPDATE',
    'CASH_REGISTERS_READ'
  )
  AND NOT EXISTS (
    SELECT 1 FROM access.role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- =============================================================================
-- 4. Actualizar opciones de menú de Ventas existentes (creadas en V28)
--    para que coincidan con las rutas reales de sales.routes.ts
-- =============================================================================

-- ID 4: "Cuentas abiertas" → ahora es "Cuentas" con ruta /sales/accounts
UPDATE access.system_menu_options
SET name = 'Cuentas',
    route = '/sales/accounts',
    icon = 'receipt_long',
    permission_id = (SELECT id FROM access.permissions WHERE code = 'ACCOUNTS_READ'),
    sort_order = 1,
    updated_at = NOW()
WHERE id = 4;

-- ID 5: "Nueva venta" → ahora es "Historial de Ventas" (la ruta /sales/new no existe)
UPDATE access.system_menu_options
SET name = 'Historial de Ventas',
    route = '/sales/history',
    icon = 'point_of_sale',
    permission_id = (SELECT id FROM access.permissions WHERE code = 'SALES_READ'),
    sort_order = 2,
    updated_at = NOW()
WHERE id = 5;

-- ID 6: Era "Historial" duplicado → ahora es "Clientes"
UPDATE access.system_menu_options
SET name = 'Clientes',
    route = '/sales/customers',
    icon = 'people',
    permission_id = (SELECT id FROM access.permissions WHERE code = 'SALES_CUSTOMERS'),
    sort_order = 3,
    updated_at = NOW()
WHERE id = 6;

-- =============================================================================
-- 5. Agregar opción de menú faltante: Cajas Registradoras
-- =============================================================================
INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (23, 'Cajas Registradoras', '/sales/cash-registers', 'payments', 3,
  (SELECT id FROM access.permissions WHERE code = 'CASH_REGISTERS_READ'), 4);

-- Resetear secuencia
SELECT setval('access.system_menu_options_id_seq', (SELECT MAX(id) FROM access.system_menu_options));
