SET search_path TO access;

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
