package drinks.system.salesservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CloseAccountRequest(
        Long customerId,
        @NotNull Long cashRegisterId,
        @NotBlank String paymentMethod,
        BigDecimal discountAmount
) {}
