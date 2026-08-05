package drinks.system.inventoryservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PurchaseOrder(
        Long id, Long supplierId, Long branchId, String orderNumber,
        String status, BigDecimal totalAmount, Instant orderDate,
        Instant receivedDate, Instant createdAt, Instant updatedAt,
        Long createdBy, Long updatedBy, List<PurchaseOrderDetail> details
) {}
