-- ============================================================================
-- V8__seed_admin_user.sql
-- Admin user with ADMINISTRADOR_SISTEMA role (full access)
-- Password: Admin123! (BCrypt 10 rounds)
-- ============================================================================

SET search_path TO access;

-- =============================================================================
-- Usuario administrador inicial
-- Password: Admin123! → BCrypt hash
-- =============================================================================
INSERT INTO access.users (username, password_hash, email, full_name, is_active)
VALUES ('admin', '$2a$10$053oYVNjo6fzta/y5ULJqOR8kU5IkIi/24rwAcckpprpzDZS8O/Ky',
        'admin@system.local', 'Administrador del Sistema', true);

-- =============================================================================
-- Asignar rol ADMINISTRADOR_SISTEMA
-- =============================================================================
INSERT INTO access.user_roles (user_id, role_id)
SELECT u.id, r.id
FROM access.users u, access.roles r
WHERE u.username = 'admin' AND r.code = 'ADMINISTRADOR_SISTEMA';
