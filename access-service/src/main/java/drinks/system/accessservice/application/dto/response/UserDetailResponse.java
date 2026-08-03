package drinks.system.accessservice.application.dto.response;

import java.time.Instant;
import java.util.List;

public record UserDetailResponse(
        Long id,
        String username,
        String email,
        String fullName,
        Long branchId,
        Boolean isActive,
        Instant lastLogin,
        Instant deletedAt,
        Instant createdAt,
        Instant updatedAt,
        Long createdBy,
        Long updatedBy,
        List<RoleResponse> roles,
        List<BranchResponse> branches
) {
}
