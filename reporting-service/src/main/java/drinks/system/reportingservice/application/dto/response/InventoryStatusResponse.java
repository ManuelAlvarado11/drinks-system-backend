package drinks.system.reportingservice.application.dto.response;

import java.math.BigDecimal;

public record InventoryStatusResponse(
        Long id, Long productId, Long branchId, String productName,
        String categoryName, Integer currentStock, Integer minimumStock,
        BigDecimal costPrice, BigDecimal salePrice, Boolean isLowStock
) {}
