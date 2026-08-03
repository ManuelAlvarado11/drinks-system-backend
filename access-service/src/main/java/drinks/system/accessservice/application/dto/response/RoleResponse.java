package drinks.system.accessservice.application.dto.response;

import java.time.Instant;

public record RoleResponse(
        Long id,
        String code,
        String name,
        String description,
        Boolean isActive,
        Instant createdAt,
        int permissionCount,
        int userCount
) {
}
