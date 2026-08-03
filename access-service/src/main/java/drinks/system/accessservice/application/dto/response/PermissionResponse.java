package drinks.system.accessservice.application.dto.response;

public record PermissionResponse(
        Long id,
        String code,
        String name,
        String description,
        String module,
        Boolean isActive
) {
}
