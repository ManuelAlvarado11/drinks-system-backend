package drinks.system.accessservice.application.dto.response;

import java.time.Instant;

public record CatalogResponse(
        Long id,
        String catalogType,
        String code,
        String name,
        String description,
        Integer sortOrder,
        Boolean isActive,
        Long parentId,
        Instant createdAt,
        Instant updatedAt
) {
}
