package drinks.system.accessservice.application.dto.response;

import java.time.Instant;
import java.util.List;

public record UserResponse(
        Long id,
        String username,
        String email,
        String fullName,
        Long branchId,
        Boolean isActive,
        Instant lastLogin,
        Instant createdAt,
        List<String> roles
) {
}
