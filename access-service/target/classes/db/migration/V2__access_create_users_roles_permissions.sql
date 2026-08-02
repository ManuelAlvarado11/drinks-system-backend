-- V2__access_create_users_roles_permissions.sql
-- Creación de tablas de usuarios, roles y permisos en el schema access

SET search_path TO access;

-- =============================================================================
-- Tabla: users
-- Almacena los usuarios del sistema con credenciales y referencia a sucursal
-- =============================================================================
CREATE TABLE access.users (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    email           VARCHAR(150) NOT NULL,
    full_name       VARCHAR(200) NOT NULL,
    branch_id       BIGINT REFERENCES access.branches(id) ON DELETE SET NULL,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    last_login      TIMESTAMPTZ,
    deleted_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT
);

CREATE INDEX idx_users_branch_id ON access.users(branch_id);
CREATE INDEX idx_users_username ON access.users(username);
CREATE INDEX idx_users_is_active ON access.users(is_active) WHERE is_active = true;
CREATE INDEX idx_users_email ON access.users(email);

-- =============================================================================
-- Tabla: roles
-- Define los roles del sistema (ADMINISTRADOR_SISTEMA, GERENTE_SUCURSAL, etc.)
-- =============================================================================
CREATE TABLE access.roles (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(50) NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    description     VARCHAR(300),
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- =============================================================================
-- Tabla: permissions
-- Define los permisos granulares por módulo del sistema
-- =============================================================================
CREATE TABLE access.permissions (
    id              BIGSERIAL PRIMARY KEY,
    code            VARCHAR(80) NOT NULL UNIQUE,
    name            VARCHAR(150) NOT NULL,
    description     VARCHAR(300),
    module          VARCHAR(50) NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_permissions_module ON access.permissions(module);

-- =============================================================================
-- Tabla: role_permissions (N:M entre roles y permisos)
-- Asigna permisos a roles
-- =============================================================================
CREATE TABLE access.role_permissions (
    id              BIGSERIAL PRIMARY KEY,
    role_id         BIGINT NOT NULL REFERENCES access.roles(id) ON DELETE CASCADE,
    permission_id   BIGINT NOT NULL REFERENCES access.permissions(id) ON DELETE CASCADE,
    UNIQUE(role_id, permission_id)
);

CREATE INDEX idx_role_permissions_role_id ON access.role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON access.role_permissions(permission_id);

-- =============================================================================
-- Tabla: user_roles (N:M entre usuarios y roles)
-- Asigna roles a usuarios
-- =============================================================================
CREATE TABLE access.user_roles (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES access.users(id) ON DELETE CASCADE,
    role_id         BIGINT NOT NULL REFERENCES access.roles(id) ON DELETE RESTRICT,
    UNIQUE(user_id, role_id)
);

CREATE INDEX idx_user_roles_user_id ON access.user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON access.user_roles(role_id);

-- =============================================================================
-- Tabla: user_branches (N:M entre usuarios y sucursales)
-- Permite asignar usuarios a múltiples sucursales
-- =============================================================================
CREATE TABLE access.user_branches (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES access.users(id) ON DELETE CASCADE,
    branch_id       BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE CASCADE,
    UNIQUE(user_id, branch_id)
);

CREATE INDEX idx_user_branches_user_id ON access.user_branches(user_id);
CREATE INDEX idx_user_branches_branch_id ON access.user_branches(branch_id);
