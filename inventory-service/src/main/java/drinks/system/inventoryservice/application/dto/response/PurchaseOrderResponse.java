package drinks.system.inventoryservice.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record PurchaseOrderResponse(
        Long id, Long supplierId, Long branchId, String orderNumber,
        String status, BigDecimal totalAmount, Instant orderDate,
        Instant receivedDate, Long createdBy
) {}
