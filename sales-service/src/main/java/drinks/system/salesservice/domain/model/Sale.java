package drinks.system.salesservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record Sale(
        Long id,
        Long branchId,
        Long accountId,
        Long customerId,
        Long cashRegisterId,
        String saleNumber,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String paymentMethod,
        String status,
        Instant saleDate,
        Instant createdAt,
        Instant updatedAt,
        Long createdBy,
        Long updatedBy,
        List<SaleDetail> details
) {
}
