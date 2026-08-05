package drinks.system.inventoryservice.domain.model;

import java.time.Instant;

public record Category(
        Long id, String name, String description, Long parentCategoryId,
        Boolean isActive, Instant deletedAt, Instant createdAt, Instant updatedAt,
        Long createdBy, Long updatedBy
) {}
