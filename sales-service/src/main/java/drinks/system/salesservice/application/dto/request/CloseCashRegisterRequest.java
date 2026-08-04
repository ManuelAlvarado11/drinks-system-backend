package drinks.system.salesservice.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CloseCashRegisterRequest(
        @NotNull BigDecimal closingAmount,
        String notes
) {}
