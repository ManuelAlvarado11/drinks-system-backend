package drinks.system.accessservice.application.dto.response;

import java.time.Instant;

public record BranchResponse(
        Long id,
        String name,
        String address,
        String phone,
        String email,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}
