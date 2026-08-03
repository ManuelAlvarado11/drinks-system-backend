SET search_path TO access;

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
