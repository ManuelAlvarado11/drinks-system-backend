package drinks.system.salesservice.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record AddAccountItemRequest(
        @NotNull Long productId,
        @NotNull @Positive Integer quantity,
        @NotNull BigDecimal unitPrice
) {}
