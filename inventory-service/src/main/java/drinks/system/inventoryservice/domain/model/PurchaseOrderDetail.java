package drinks.system.inventoryservice.domain.model;

import java.math.BigDecimal;

public record PurchaseOrderDetail(
        Long id, Long purchaseOrderId, Long productId,
        Integer quantityOrdered, Integer quantityReceived,
        BigDecimal unitCost, BigDecimal subtotal
) {}
