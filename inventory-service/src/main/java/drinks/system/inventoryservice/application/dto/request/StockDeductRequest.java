package drinks.system.inventoryservice.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record StockDeductRequest(
        @NotNull Long branchId,
        @NotEmpty List<StockItem> items
) {
    public record StockItem(
            @NotNull Long productId,
            @NotNull Integer quantity
    ) {}
}
