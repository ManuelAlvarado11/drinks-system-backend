package drinks.system.inventoryservice.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id, String code, String name, Long categoryId, String size,
        String description, BigDecimal costPrice, BigDecimal salePrice,
        Boolean isActive, Instant createdAt, Instant updatedAt
) {}
