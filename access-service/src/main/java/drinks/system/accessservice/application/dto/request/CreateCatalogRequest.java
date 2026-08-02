package drinks.system.accessservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCatalogRequest(
        @NotBlank @Size(max = 50) String catalogType,
        @NotBlank @Size(max = 50) String code,
        @NotBlank @Size(max = 150) String name,
        @Size(max = 300) String description,
        Integer sortOrder,
        Long parentId
) {
}
