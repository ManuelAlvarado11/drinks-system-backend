package drinks.system.inventoryservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 300) String description,
        @Size(max = 50) String icon,
        Long parentCategoryId
) {}
