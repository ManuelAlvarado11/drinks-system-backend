package drinks.system.accessservice.application.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateMenuOptionRequest(
        @Size(max = 100) String name,
        @Size(max = 200) String route,
        @Size(max = 50) String icon,
        Long parentId,
        Long permissionId,
        Integer sortOrder
) {
}
