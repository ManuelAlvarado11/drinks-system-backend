SET search_path TO inventory;

CREATE TABLE inventory.categories (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    description         VARCHAR(300),
    parent_category_id  BIGINT REFERENCES inventory.categories(id) ON DELETE SET NULL,
    is_active           BOOLEAN NOT NULL DEFAULT true,
    deleted_at          TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT
);

CREATE INDEX idx_categories_parent ON inventory.categories(parent_category_id);
CREATE INDEX idx_categories_is_active ON inventory.categories(is_active) WHERE is_active = true;
