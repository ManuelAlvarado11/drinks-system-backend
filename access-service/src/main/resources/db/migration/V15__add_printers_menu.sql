-- ============================================================================
-- V15__add_printers_menu.sql
-- Adds the "Impresoras" menu option under the Administración group (id = 20).
-- Uses the existing CONFIG_PARAMS permission (already granted to
-- ADMINISTRADOR_SISTEMA), so no permission changes are needed.
-- ============================================================================

SET search_path TO access;

INSERT INTO access.system_menu_options (name, route, icon, parent_id, permission_id, sort_order)
VALUES (
    'Impresoras',
    '/administration/printers',
    'print',
    20,
    (SELECT id FROM access.permissions WHERE code = 'CONFIG_PARAMS'),
    6
);

-- Keep the sequence aligned after the insert.
SELECT setval('access.system_menu_options_id_seq', (SELECT MAX(id) FROM access.system_menu_options));
