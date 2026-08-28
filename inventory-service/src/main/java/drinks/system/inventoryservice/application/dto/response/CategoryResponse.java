package drinks.system.inventoryservice.application.dto.response;

import java.time.Instant;

public record CategoryResponse(
        Long id, String name, String description, String icon, Long parentCategoryId,
        Boolean isActive, Instant createdAt, Instant updatedAt
) {}
