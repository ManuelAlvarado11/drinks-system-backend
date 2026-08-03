package drinks.system.accessservice.application.dto.response;

import java.time.Instant;

public record MenuOptionResponse(
        Long id,
        String name,
        String route,
        String icon,
        Long parentId,
        Long permissionId,
        Integer sortOrder,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}
