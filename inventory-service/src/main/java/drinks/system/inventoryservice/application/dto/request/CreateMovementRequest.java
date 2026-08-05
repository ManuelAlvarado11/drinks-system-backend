package drinks.system.inventoryservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMovementRequest(
        @NotNull Long productId,
        @NotNull Long branchId,
        @NotBlank String movementType,
        @NotNull Integer quantity,
        String notes
) {}
