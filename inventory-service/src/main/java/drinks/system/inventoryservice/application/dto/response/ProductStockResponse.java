package drinks.system.inventoryservice.application.dto.response;

import java.time.Instant;

public record ProductStockResponse(
        Long id, Long productId, Long branchId,
        String productName,
        Integer currentStock, Integer minimumStock,
        Boolean isLowStock, Instant updatedAt
) {}
