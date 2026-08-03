package drinks.system.accessservice.application.dto.response;

import java.util.List;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String fullName,
        Long branchId,
        List<String> roles,
        List<String> permissions
) {
}
