-- V32__add_account_action_permissions.sql
-- Agrega permisos para acciones sobre cuentas: agregar items, cancelar cuenta
-- y cancelar items individuales. Asigna al ADMINISTRADOR_SISTEMA y CAJERO.

SET search_path TO access;

-- =============================================================================
-- 1. Permisos nuevos
-- =============================================================================
INSERT INTO access.permissions (code, name, module) VALUES
('ACCOUNTS_ADD_ITEMS', 'Agregar productos a cuenta', 'VENTAS'),
('ACCOUNTS_CANCEL', 'Cancelar cuenta', 'VENTAS'),
('ACCOUNTS_CANCEL_ITEMS', 'Cancelar items de cuenta', 'VENTAS');

-- =============================================================================
-- 2. Asignar TODOS al ADMINISTRADOR_SISTEMA
-- =============================================================================
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM access.roles r
CROSS JOIN access.permissions p
WHERE r.code = 'ADMINISTRADOR_SISTEMA'
  AND p.code IN ('ACCOUNTS_ADD_ITEMS', 'ACCOUNTS_CANCEL', 'ACCOUNTS_CANCEL_ITEMS')
  AND NOT EXISTS (
    SELECT 1 FROM access.role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- =============================================================================
-- 3. Asignar permisos operativos al CAJERO (solo agregar items)
-- =============================================================================
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM access.roles r
CROSS JOIN access.permissions p
WHERE r.code = 'CAJERO'
  AND p.code IN ('ACCOUNTS_ADD_ITEMS')
  AND NOT EXISTS (
    SELECT 1 FROM access.role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
