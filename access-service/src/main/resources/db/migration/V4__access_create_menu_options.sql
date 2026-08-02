SET search_path TO access;

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
