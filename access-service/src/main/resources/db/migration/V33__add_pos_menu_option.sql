-- V33__add_pos_menu_option.sql
-- Agrega la opción de menú POS al grupo Ventas y reordena los items existentes.

SET search_path TO access;

-- =============================================================================
-- 1. Insertar opción POS como primer item del grupo Ventas (parent_id = 3)
-- =============================================================================
INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (24, 'POS', '/sales/pos', 'point_of_sale', 3,
  (SELECT id FROM access.permissions WHERE code = 'ACCOUNTS_CREATE'), 1);

-- =============================================================================
-- 2. Reordenar items existentes del grupo Ventas
-- =============================================================================
UPDATE access.system_menu_options SET sort_order = 2, updated_at = NOW() WHERE id = 4;  -- Cuentas
UPDATE access.system_menu_options SET sort_order = 3, updated_at = NOW() WHERE id = 5;  -- Historial
UPDATE access.system_menu_options SET sort_order = 4, updated_at = NOW() WHERE id = 6;  -- Clientes
UPDATE access.system_menu_options SET sort_order = 5, updated_at = NOW() WHERE id = 23; -- Cajas

-- Resetear secuencia
SELECT setval('access.system_menu_options_id_seq', (SELECT MAX(id) FROM access.system_menu_options));
