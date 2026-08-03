-- V6__access_create_catalogs.sql
-- Creación de la tabla catalogs en el schema access

SET search_path TO access;

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
