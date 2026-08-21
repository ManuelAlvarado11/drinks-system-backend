package drinks.system.inventoryservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductPresentation(
        Long id, Long productId, String name, Integer quantity,
        BigDecimal price, Boolean isActive, Integer sortOrder,
        Instant createdAt, Instant updatedAt
) {}
