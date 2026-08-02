package drinks.system.accessservice.domain.model;

import java.time.Instant;

/**
 * Modelo de dominio que representa un registro de auditoría.
 * Captura todas las operaciones de escritura realizadas en el sistema,
 * incluyendo valores anteriores y posteriores para trazabilidad completa.
 */
public record AuditLog(
        Long id,
        Long userId,
        String username,
        String action,
        String module,
        String entityName,
        Long entityId,
        String oldValues,
        String newValues,
        String ipAddress,
        String description,
        Instant createdAt
) {
}
