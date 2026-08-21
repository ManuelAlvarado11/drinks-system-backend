package drinks.system.inventoryservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank @Size(max = 50) String code,
        @NotBlank @Size(max = 150) String name,
        Long categoryId,
        @Size(max = 50) String size,
        String description,
        @NotNull BigDecimal costPrice,
        @NotNull BigDecimal salePrice,
        Boolean tracksInventory
) {}
