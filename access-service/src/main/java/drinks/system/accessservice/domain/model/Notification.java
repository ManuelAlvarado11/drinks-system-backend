package drinks.system.accessservice.domain.model;

import java.time.Instant;

/**
 * Modelo de dominio que representa una notificación del sistema.
 * Las notificaciones son mensajes dirigidos a usuarios específicos
 * sobre eventos relevantes como stock bajo o alertas del sistema.
 */
public record Notification(
        Long id,
        Long branchId,
        Long userId,
        String notificationType,
        String title,
        String message,
        String entityName,
        Long entityId,
        Boolean isRead,
        Instant createdAt,
        Instant readAt
) {
}
