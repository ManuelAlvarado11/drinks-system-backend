package drinks.system.salesservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record CreateDirectSaleRequest(
        @NotNull Long branchId,
        @NotNull Long cashRegisterId,
        Long customerId,
        @NotBlank String paymentMethod,
        BigDecimal discountAmount,
        @NotEmpty List<SaleItemRequest> items
) {
    public record SaleItemRequest(
            @NotNull Long productId,
            @NotNull Integer quantity,
            @NotNull BigDecimal unitPrice
    ) {}
}
