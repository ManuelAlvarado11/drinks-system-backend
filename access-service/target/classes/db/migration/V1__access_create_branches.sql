-- V1__access_create_branches.sql
-- Creación de la tabla branches en el schema access

SET search_path TO access;

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

-- Índice parcial para consultas frecuentes sobre sucursales activas
CREATE INDEX idx_branches_is_active ON access.branches(is_active) WHERE is_active = true;
