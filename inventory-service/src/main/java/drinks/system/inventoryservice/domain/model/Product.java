package drinks.system.inventoryservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Product(
        Long id, String code, String name, Long categoryId, String size,
        String description, BigDecimal costPrice, BigDecimal salePrice,
        Boolean tracksInventory, Boolean isActive, Instant deletedAt,
        Instant createdAt, Instant updatedAt, Long createdBy, Long updatedBy
) {}
