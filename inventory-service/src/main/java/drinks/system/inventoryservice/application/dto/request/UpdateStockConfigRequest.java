package drinks.system.inventoryservice.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateStockConfigRequest(
        @NotNull Integer minimumStock
) {}
