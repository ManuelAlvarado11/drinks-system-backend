package drinks.system.inventoryservice.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateProductPresentationRequest(
        @Size(max = 50) String name,
        @Min(2) Integer quantity,
        BigDecimal price,
        Boolean isActive,
        Integer sortOrder
) {}
