package drinks.system.inventoryservice.domain.model;

import java.time.Instant;

public record ProductStock(
        Long id, Long productId, Long branchId,
        Integer currentStock, Integer minimumStock, Instant updatedAt
) {}
