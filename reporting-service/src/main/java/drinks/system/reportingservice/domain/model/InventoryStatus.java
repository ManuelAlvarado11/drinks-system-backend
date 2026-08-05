package drinks.system.reportingservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record InventoryStatus(
        Long id, Long productId, Long branchId, String productName,
        String categoryName, Integer currentStock, Integer minimumStock,
        BigDecimal costPrice, BigDecimal salePrice, Boolean isLowStock, Instant refreshedAt
) {}
