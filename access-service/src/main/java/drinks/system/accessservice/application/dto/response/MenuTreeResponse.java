package drinks.system.accessservice.application.dto.response;

import java.util.List;

public record MenuTreeResponse(
        Long id,
        String name,
        String route,
        String icon,
        Integer sortOrder,
        List<MenuTreeResponse> children
) {
}
