package drinks.system.accessservice.application.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCatalogRequest(
        @Size(max = 150) String name,
        @Size(max = 300) String description,
        Integer sortOrder,
        Long parentId
) {
}
