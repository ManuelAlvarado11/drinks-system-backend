package drinks.system.inventoryservice.application.dto.response;

import java.math.BigDecimal;

public record ProductPresentationResponse(
        Long id, Long productId, String name, Integer quantity,
        BigDecimal price, BigDecimal unitPrice, Boolean isActive, Integer sortOrder
) {}
