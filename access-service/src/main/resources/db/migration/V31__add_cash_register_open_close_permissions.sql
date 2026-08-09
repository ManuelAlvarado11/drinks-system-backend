-- V31__add_cash_register_and_account_permissions.sql
-- Agrega permisos CASH_REGISTERS_OPEN y CASH_REGISTERS_CLOSE que faltaban en V30
-- y los asigna al ADMINISTRADOR_SISTEMA y al CAJERO.

SET search_path TO access;

-- =============================================================================
-- 1. Permisos nuevos
-- =============================================================================
INSERT INTO access.permissions (code, name, module) VALUES
('CASH_REGISTERS_OPEN', 'Abrir caja registradora', 'CAJA'),
('CASH_REGISTERS_CLOSE', 'Cerrar caja registradora', 'CAJA');

-- =============================================================================
-- 2. Asignar al ADMINISTRADOR_SISTEMA
-- =============================================================================
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM access.roles r
CROSS JOIN access.permissions p
WHERE r.code = 'ADMINISTRADOR_SISTEMA'
  AND p.code IN ('CASH_REGISTERS_OPEN', 'CASH_REGISTERS_CLOSE')
  AND NOT EXISTS (
    SELECT 1 FROM access.role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- =============================================================================
-- 3. Asignar al CAJERO
-- =============================================================================
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM access.roles r
CROSS JOIN access.permissions p
WHERE r.code = 'CAJERO'
  AND p.code IN ('CASH_REGISTERS_OPEN', 'CASH_REGISTERS_CLOSE')
  AND NOT EXISTS (
    SELECT 1 FROM access.role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
