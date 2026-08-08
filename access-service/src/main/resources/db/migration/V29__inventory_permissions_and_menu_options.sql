-- V29__inventory_permissions_and_menu_options.sql
-- Crea permisos granulares de inventario faltantes, actualiza opciones de menú
-- existentes y agrega las que faltan (Categorías, Proveedores).
-- Asigna todos los permisos nuevos al rol ADMINISTRADOR_SISTEMA.

SET search_path TO access;

-- =============================================================================
-- 1. Permisos nuevos de inventario (no existían en V23 ni V28)
-- =============================================================================
INSERT INTO access.permissions (code, name, module) VALUES
('INVENTORY_PRODUCTS', 'Gestión de productos', 'INVENTARIO'),
('INVENTORY_CATEGORIES', 'Gestión de categorías', 'INVENTARIO'),
('INVENTORY_STOCK', 'Consultar existencias', 'INVENTARIO'),
('INVENTORY_SUPPLIERS', 'Gestión de proveedores', 'INVENTARIO');

-- =============================================================================
-- 2. Asignar los permisos nuevos al rol ADMINISTRADOR_SISTEMA
-- =============================================================================
INSERT INTO access.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM access.roles r
CROSS JOIN access.permissions p
WHERE r.code = 'ADMINISTRADOR_SISTEMA'
  AND p.code IN (
    'INVENTORY_PRODUCTS',
    'INVENTORY_CATEGORIES',
    'INVENTORY_STOCK',
    'INVENTORY_SUPPLIERS'
  )
  AND NOT EXISTS (
    SELECT 1 FROM access.role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- =============================================================================
-- 3. Actualizar opciones de menú de inventario existentes (V28)
--    para que apunten a los permisos específicos del frontend
-- =============================================================================

-- ID 8: Productos → INVENTORY_PRODUCTS (antes apuntaba a PRODUCTS_CREATE)
UPDATE access.system_menu_options
SET permission_id = (SELECT id FROM access.permissions WHERE code = 'INVENTORY_PRODUCTS'),
    updated_at = NOW()
WHERE id = 8;

-- ID 9: Existencias → INVENTORY_STOCK (antes apuntaba a INVENTORY_READ)
UPDATE access.system_menu_options
SET permission_id = (SELECT id FROM access.permissions WHERE code = 'INVENTORY_STOCK'),
    updated_at = NOW()
WHERE id = 9;

-- ID 10: Movimientos → INVENTORY_MOVEMENTS (ya estaba correcto, solo confirmar)
-- No requiere cambio, INVENTORY_MOVEMENTS ya existía.

-- ID 11: Ajustes → Eliminamos esta opción que no coincide con el frontend
--         El frontend no tiene ruta /inventory/adjustments; la reemplazamos por Proveedores
UPDATE access.system_menu_options
SET name = 'Proveedores',
    route = '/inventory/suppliers',
    icon = 'local_shipping',
    permission_id = (SELECT id FROM access.permissions WHERE code = 'INVENTORY_SUPPLIERS'),
    sort_order = 5,
    updated_at = NOW()
WHERE id = 11;

-- =============================================================================
-- 4. Agregar opción de menú faltante: Categorías (entre Productos y Existencias)
-- =============================================================================
INSERT INTO access.system_menu_options (id, name, route, icon, parent_id, permission_id, sort_order)
VALUES (22, 'Categorías', '/inventory/categories', 'category', 7,
  (SELECT id FROM access.permissions WHERE code = 'INVENTORY_CATEGORIES'), 2);

-- Reordenar items existentes del grupo Inventario para acomodar Categorías
UPDATE access.system_menu_options SET sort_order = 1, updated_at = NOW() WHERE id = 8;  -- Productos
-- ID 22 Categorías ya tiene sort_order = 2
UPDATE access.system_menu_options SET sort_order = 3, updated_at = NOW() WHERE id = 9;  -- Existencias
UPDATE access.system_menu_options SET sort_order = 4, updated_at = NOW() WHERE id = 10; -- Movimientos
-- ID 11 Proveedores ya tiene sort_order = 5

-- Resetear secuencia
SELECT setval('access.system_menu_options_id_seq', (SELECT MAX(id) FROM access.system_menu_options));
