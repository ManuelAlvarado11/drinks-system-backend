package drinks.system.salesservice.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record OpenAccountRequest(
        @NotNull Long branchId,
        String customerName,
        String customerLastName,
        String tableNumber,
        String internalCode,
        String notes
) {}
