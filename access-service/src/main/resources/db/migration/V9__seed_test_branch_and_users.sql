-- ============================================================================
-- V9__seed_test_branch_and_users.sql
-- Test branch "Sucursal Test" + Gerente and Cajero users with their roles.
-- Passwords: admin123 for both (CAMBIAR EN PRODUCCION)
-- ============================================================================

SET search_path TO access;

-- =============================================================================
-- Sucursal de prueba
-- =============================================================================
INSERT INTO access.branches (name, address, phone, email, is_active)
VALUES ('Sucursal Test', 'Av. Principal #123', '70012345', 'sucursal.test@system.local', true);

-- =============================================================================
-- Usuario: gerente (Gerente de Sucursal Test)
-- Password: admin123 (CAMBIAR EN PRODUCCION)
-- =============================================================================
INSERT INTO access.users (username, password_hash, email, full_name, branch_id, is_active)
VALUES ('gerente',
        '$2a$10$053oYVNjo6fzta/y5ULJqOR8kU5IkIi/24rwAcckpprpzDZS8O/Ky',
        'gerente@system.local',
        'Gerente Sucursal Test',
        (SELECT id FROM access.branches WHERE name = 'Sucursal Test'),
        true);

-- Asignar rol GERENTE_SUCURSAL
INSERT INTO access.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM access.users u, access.roles r
WHERE u.username = 'gerente' AND r.code = 'GERENTE_SUCURSAL';

-- Asignar la sucursal en user_branches
INSERT INTO access.user_branches (user_id, branch_id)
SELECT u.id, b.id
FROM access.users u, access.branches b
WHERE u.username = 'gerente' AND b.name = 'Sucursal Test';

-- =============================================================================
-- Usuario: cajero (Cajero en Sucursal Test)
-- Password: admin123 (CAMBIAR EN PRODUCCION)
-- =============================================================================
INSERT INTO access.users (username, password_hash, email, full_name, branch_id, is_active)
VALUES ('cajero',
        '$2a$10$053oYVNjo6fzta/y5ULJqOR8kU5IkIi/24rwAcckpprpzDZS8O/Ky',
        'cajero@system.local',
        'Cajero Sucursal Test',
        (SELECT id FROM access.branches WHERE name = 'Sucursal Test'),
        true);

-- Asignar rol CAJERO
INSERT INTO access.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM access.users u, access.roles r
WHERE u.username = 'cajero' AND r.code = 'CAJERO';

-- Asignar la sucursal en user_branches
INSERT INTO access.user_branches (user_id, branch_id)
SELECT u.id, b.id
FROM access.users u, access.branches b
WHERE u.username = 'cajero' AND b.name = 'Sucursal Test';
