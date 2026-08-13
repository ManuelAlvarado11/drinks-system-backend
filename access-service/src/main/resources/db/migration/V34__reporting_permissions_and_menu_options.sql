-- V34__reporting_permissions_and_menu_options.sql
-- Crea el permiso REPORTING_READ, actualiza las opciones de menú del módulo
-- Reportes para coincidir con las rutas actuales, y asigna al ADMINISTRADOR_SISTEMA.

SET search_path TO access;

-- =============================================================================
-- 1. Permiso nuevo
-- =============================================================================
INSERT INTO access.permissions (code, name, module) VALUES
('REPORTING_READ', 'Ver reportes del sistema', 'REPORTES');

-- =============================================================================
-- 2. Asignar al ADMINISTRADOR_SISTEMA
-- =============================================================================
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM access.roles r
CROSS JOIN access.permissions p
WHERE r.code = 'ADMINISTRADOR_SISTEMA'
  AND p.code = 'REPORTING_READ'
  AND NOT EXISTS (
    SELECT 1 FROM access.role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- Asignar también al GERENTE_SUCURSAL (puede ver reportes de su sucursal)
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM access.roles r
CROSS JOIN access.permissions p
WHERE r.code = 'GERENTE_SUCURSAL'
  AND p.code = 'REPORTING_READ'
  AND NOT EXISTS (
    SELECT 1 FROM access.role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- =============================================================================
-- 3. Actualizar opciones de menú del grupo Reportes (parent_id = 12)
--    Las rutas antiguas (/reporting/sales, /inventory, /dashboard) ya no existen.
-- =============================================================================

-- ID 13: "Ventas" → ahora "Ventas Diarias"
UPDATE access.system_menu_options
SET name = 'Ventas Diarias',
    route = '/reporting/daily-sales',
    icon = 'today',
    permission_id = (SELECT id FROM access.permissions WHERE code = 'REPORTING_READ'),
    sort_order = 1,
    updated_at = NOW()
WHERE id = 13;

-- ID 14: "Inventario" → ahora "Ventas Mensuales"
UPDATE access.system_menu_options
SET name = 'Ventas Mensuales',
    route = '/reporting/monthly-sales',
    icon = 'calendar_month',
    permission_id = (SELECT id FROM access.permissions WHERE code = 'REPORTING_READ'),
    sort_order = 2,
    updated_at = NOW()
WHERE id = 14;

-- ID 15: "Dashboard" → ahora "Ranking de Productos"
UPDATE access.system_menu_options
SET name = 'Ranking de Productos',
    route = '/reporting/product-ranking',
    icon = 'leaderboard',
    permission_id = (SELECT id FROM access.permissions WHERE code = 'REPORTING_READ'),
    sort_order = 3,
    updated_at = NOW()
WHERE id = 15;

-- =============================================================================
-- 4. Agregar opción faltante: Estado de Inventario
-- =============================================================================
INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (25, 'Estado de Inventario', '/reporting/inventory-status', 'inventory', 12,
  (SELECT id FROM access.permissions WHERE code = 'REPORTING_READ'), 4);

-- Resetear secuencia
SELECT setval('access.system_menu_options_id_seq', (SELECT MAX(id) FROM access.system_menu_options));
