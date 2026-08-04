package drinks.system.salesservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateMovementRequest(
        @NotBlank String movementType,
        @NotNull BigDecimal amount,
        String description
) {}
