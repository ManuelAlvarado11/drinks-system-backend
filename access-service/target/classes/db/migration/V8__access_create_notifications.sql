-- V8__access_create_notifications.sql
-- Creación de la tabla notifications en el schema access

SET search_path TO access;

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

-- Índice compuesto para consultas de notificaciones por usuario y estado de lectura
CREATE INDEX idx_notifications_user_read ON access.notifications(user_id, is_read);

-- Índice para filtrar notificaciones por sucursal
CREATE INDEX idx_notifications_branch_id ON access.notifications(branch_id);

-- Índice para ordenar notificaciones por fecha de creación
CREATE INDEX idx_notifications_created_at ON access.notifications(created_at);
