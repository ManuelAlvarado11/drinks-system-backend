SET search_path TO inventory;

CREATE TABLE inventory.suppliers (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    contact_name    VARCHAR(150),
    phone           VARCHAR(20),
    email           VARCHAR(150),
    address         VARCHAR(300),
    nit             VARCHAR(30),
    is_active       BOOLEAN NOT NULL DEFAULT true,
    deleted_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT
);

CREATE INDEX idx_suppliers_is_active ON inventory.suppliers(is_active) WHERE is_active = true;
CREATE INDEX idx_suppliers_nit ON inventory.suppliers(nit);
