package drinks.system.inventoryservice.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PurchaseOrderDetailResponse(
        Long id, Long supplierId, Long branchId, String orderNumber,
        String status, BigDecimal totalAmount, Instant orderDate,
        Instant receivedDate, Long createdBy,
        List<PurchaseItemResponse> items
) {
    public record PurchaseItemResponse(
            Long id, Long productId, Integer quantityOrdered,
            Integer quantityReceived, BigDecimal unitCost, BigDecimal subtotal
    ) {}
}
