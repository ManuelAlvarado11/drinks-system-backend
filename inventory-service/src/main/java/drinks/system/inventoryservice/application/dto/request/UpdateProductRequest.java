package drinks.system.inventoryservice.application.dto.request;

import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateProductRequest(
        @Size(max = 150) String name,
        Long categoryId,
        @Size(max = 50) String size,
        String description,
        BigDecimal costPrice,
        BigDecimal salePrice
) {}
