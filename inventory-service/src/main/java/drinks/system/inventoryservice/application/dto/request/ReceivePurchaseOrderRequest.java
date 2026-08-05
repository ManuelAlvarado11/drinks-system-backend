package drinks.system.inventoryservice.application.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ReceivePurchaseOrderRequest(
        @NotEmpty List<ReceivedItem> items
) {
    public record ReceivedItem(
            @NotNull Long detailId,
            @NotNull Integer quantityReceived
    ) {}
}
