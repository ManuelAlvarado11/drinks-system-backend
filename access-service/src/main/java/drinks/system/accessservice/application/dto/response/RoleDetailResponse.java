package drinks.system.accessservice.application.dto.response;

import java.time.Instant;
import java.util.List;

public record RoleDetailResponse(
        Long id,
        String code,
        String name,
        String description,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt,
        List<PermissionResponse> permissions
) {
}
