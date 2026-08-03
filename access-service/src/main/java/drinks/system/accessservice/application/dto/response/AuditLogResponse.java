package drinks.system.accessservice.application.dto.response;

import java.time.Instant;

public record AuditLogResponse(
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
