SET search_path TO access;

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

-- Partición default para datos sin partición específica
CREATE TABLE access.audit_logs_default PARTITION OF access.audit_logs DEFAULT;

CREATE INDEX idx_audit_logs_user_id ON access.audit_logs(user_id);
CREATE INDEX idx_audit_logs_module ON access.audit_logs(module);
CREATE INDEX idx_audit_logs_entity ON access.audit_logs(entity_name, entity_id);
CREATE INDEX idx_audit_logs_created_at ON access.audit_logs(created_at);
CREATE INDEX idx_audit_logs_action ON access.audit_logs(action);
