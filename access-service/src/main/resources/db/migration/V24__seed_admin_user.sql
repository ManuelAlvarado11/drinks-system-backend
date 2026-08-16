-- V24__seed_admin_user.sql
-- Seed de usuario administrador inicial con rol ADMINISTRADOR_SISTEMA

SET search_path TO access;

-- =============================================================================
-- Usuario admin inicial
-- Password: admin123 (BCrypt 10 rounds - CAMBIAR EN PRODUCCIÓN)
-- =============================================================================
INSERT INTO access.users (username, password_hash, email, full_name, is_active)
VALUES ('admin', '$2a$10$053oYVNjo6fzta/y5ULJqOR8kU5IkIi/24rwAcckpprpzDZS8O/Ky', 'admin@system.local', 'Administrador del Sistema', true);

-- =============================================================================
-- Asignación del rol ADMINISTRADOR_SISTEMA al usuario admin
-- =============================================================================
INSERT INTO access.user_roles (user_id, role_id)
SELECT u.id, r.id FROM access.users u, access.roles r
WHERE u.username = 'admin' AND r.code = 'ADMINISTRADOR_SISTEMA';
