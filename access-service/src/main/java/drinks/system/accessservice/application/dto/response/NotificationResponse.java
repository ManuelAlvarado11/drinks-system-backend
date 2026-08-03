package drinks.system.accessservice.application.dto.response;

import java.time.Instant;

public record NotificationResponse(
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
