-- ============================================================================
-- V13__add_sales_by_category_menu.sql
-- Adds the "Ventas por Categoría" menu option under the Reportes group.
-- The REPORTING_READ permission already exists and is already assigned to
-- ADMINISTRADOR_SISTEMA and GERENTE_SUCURSAL, so no permission changes needed.
-- ============================================================================

SET search_path TO access;

-- Insert new menu item between 'Ventas Mensuales' (sort_order=2) and
-- 'Ranking de Productos' (sort_order=3). Shift existing items down first.
UPDATE access.system_menu_options
SET sort_order = sort_order + 1
WHERE parent_id = 15
  AND sort_order >= 3;

INSERT INTO access.system_menu_options (name, route, icon, parent_id, permission_id, sort_order)
VALUES (
    'Ventas por Categoría',
    '/reporting/sales-by-category',
    'category',
    15,
    (SELECT id FROM access.permissions WHERE code = 'REPORTING_READ'),
    3
);
