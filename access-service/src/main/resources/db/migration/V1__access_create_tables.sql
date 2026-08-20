-- ============================================================================
-- V1__access_create_tables.sql
-- All tables for the ACCESS schema: branches, users, roles, permissions,
-- join tables, refresh_tokens, menu_options, system_parameters, catalogs,
-- audit_logs, notifications
-- ============================================================================

SET search_path TO access;

-- =============================================================================
-- Tabla: branches
-- =============================================================================
CREATE TABLE access.branches (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150) NOT NULL,
    address         VARCHAR(300),
    phone           VARCHAR(20),
    email           VARCHAR(150),
    is_active       BOOLEAN NOT NULL DEFAULT true,
    deleted_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT
);

CREATE INDEX idx_branches_is_active ON access.branches(is_active) WHERE is_active = true;

-- =============================================================================
-- Tabla: users
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
-- Tabla: role_permissions (N:M roles <-> permisos)
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
-- Tabla: user_roles (N:M usuarios <-> roles)
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
-- Tabla: user_branches (N:M usuarios <-> sucursales)
-- =============================================================================
CREATE TABLE access.user_branches (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES access.users(id) ON DELETE CASCADE,
    branch_id       BIGINT NOT NULL REFERENCES access.branches(id) ON DELETE CASCADE,
    UNIQUE(user_id, branch_id)
);

CREATE INDEX idx_user_branches_user_id ON access.user_branches(user_id);
CREATE INDEX idx_user_branches_branch_id ON access.user_branches(branch_id);

-- =============================================================================
-- Tabla: refresh_tokens
-- =============================================================================
CREATE TABLE access.refresh_tokens (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES access.users(id) ON DELETE CASCADE,
    token_hash      VARCHAR(512) NOT NULL,
    expires_at      TIMESTAMPTZ NOT NULL,
    is_revoked      BOOLEAN NOT NULL DEFAULT false,
    device_info     VARCHAR(300),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user_id ON access.refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON access.refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_expires_at ON access.refresh_tokens(expires_at);

-- =============================================================================
-- Tabla: system_menu_options (jerárquica, auto-referencial)
-- =============================================================================
CREATE TABLE access.system_menu_options (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    route           VARCHAR(200),
    icon            VARCHAR(50),
    parent_id       BIGINT REFERENCES access.system_menu_options(id) ON DELETE SET NULL,
    permission_id   BIGINT REFERENCES access.permissions(id) ON DELETE SET NULL,
    sort_order      INTEGER NOT NULL DEFAULT 0,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_menu_options_parent_id ON access.system_menu_options(parent_id);
CREATE INDEX idx_menu_options_permission_id ON access.system_menu_options(permission_id);

-- =============================================================================
-- Tabla: system_parameters
-- =============================================================================
CREATE TABLE access.system_parameters (
    id              BIGSERIAL PRIMARY KEY,
    parameter_key   VARCHAR(100) NOT NULL UNIQUE,
    parameter_value TEXT NOT NULL,
    data_type       VARCHAR(30) NOT NULL CHECK (data_type IN ('STRING', 'INTEGER', 'DECIMAL', 'BOOLEAN', 'JSON')),
    description     VARCHAR(300),
    module          VARCHAR(50),
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT
);

CREATE INDEX idx_system_parameters_module ON access.system_parameters(module);
CREATE INDEX idx_system_parameters_key ON access.system_parameters(parameter_key);

-- =============================================================================
-- Tabla: catalogs
-- =============================================================================
CREATE TABLE access.catalogs (
    id              BIGSERIAL PRIMARY KEY,
    catalog_type    VARCHAR(50) NOT NULL,
    code            VARCHAR(50) NOT NULL,
    name            VARCHAR(150) NOT NULL,
    description     VARCHAR(300),
    sort_order      INTEGER NOT NULL DEFAULT 0,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    parent_id       BIGINT REFERENCES access.catalogs(id) ON DELETE SET NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE(catalog_type, code)
);

CREATE INDEX idx_catalogs_type ON access.catalogs(catalog_type);
CREATE INDEX idx_catalogs_parent_id ON access.catalogs(parent_id);

-- =============================================================================
-- Tabla: audit_logs (particionada por rango de fecha)
-- =============================================================================
CREATE TABLE access.audit_logs (
    id              BIGSERIAL,
    user_id         BIGINT,
    username        VARCHAR(50),
    action          VARCHAR(50) NOT NULL,
    module          VARCHAR(50) NOT NULL,
    entity_name     VARCHAR(100) NOT NULL,
    entity_id       BIGINT,
    old_values      JSONB,
    new_values      JSONB,
    ip_address      VARCHAR(45),
    description     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

CREATE TABLE access.audit_logs_default PARTITION OF access.audit_logs DEFAULT;

CREATE INDEX idx_audit_logs_user_id ON access.audit_logs(user_id);
CREATE INDEX idx_audit_logs_module ON access.audit_logs(module);
CREATE INDEX idx_audit_logs_entity ON access.audit_logs(entity_name, entity_id);
CREATE INDEX idx_audit_logs_created_at ON access.audit_logs(created_at);
CREATE INDEX idx_audit_logs_action ON access.audit_logs(action);

-- =============================================================================
-- Tabla: notifications
-- =============================================================================
CREATE TABLE access.notifications (
    id                  BIGSERIAL PRIMARY KEY,
    branch_id           BIGINT REFERENCES access.branches(id) ON DELETE CASCADE,
    user_id             BIGINT REFERENCES access.users(id) ON DELETE CASCADE,
    notification_type   VARCHAR(50) NOT NULL,
    title               VARCHAR(200) NOT NULL,
    message             TEXT NOT NULL,
    entity_name         VARCHAR(100),
    entity_id           BIGINT,
    is_read             BOOLEAN NOT NULL DEFAULT false,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    read_at             TIMESTAMPTZ
);

CREATE INDEX idx_notifications_user_read ON access.notifications(user_id, is_read);
CREATE INDEX idx_notifications_branch_id ON access.notifications(branch_id);
CREATE INDEX idx_notifications_created_at ON access.notifications(created_at);
