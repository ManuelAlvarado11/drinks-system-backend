package drinks.system.inventoryservice.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record CreatePurchaseOrderRequest(
        @NotNull Long supplierId,
        @NotNull Long branchId,
        @NotEmpty List<PurchaseItemRequest> items
) {
    public record PurchaseItemRequest(
            @NotNull Long productId,
            @NotNull Integer quantityOrdered,
            @NotNull BigDecimal unitCost
    ) {}
}
