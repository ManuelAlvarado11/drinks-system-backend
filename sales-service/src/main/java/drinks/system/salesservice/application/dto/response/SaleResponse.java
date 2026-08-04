package drinks.system.salesservice.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record SaleResponse(
        Long id, Long branchId, Long accountId, Long customerId,
        Long cashRegisterId, String saleNumber,
        BigDecimal subtotal, BigDecimal discountAmount,
        BigDecimal taxAmount, BigDecimal totalAmount,
        String paymentMethod, String status, Instant saleDate, Long createdBy
) {}
