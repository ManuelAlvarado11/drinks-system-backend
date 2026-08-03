package drinks.system.accessservice.application.dto.response;

import java.util.List;

public record PermissionsByModuleResponse(
        String module,
        List<PermissionResponse> permissions
) {
}
