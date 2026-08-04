package drinks.system.salesservice.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SaleDetailResponse(
        Long id, Long branchId, Long accountId, Long customerId,
        Long cashRegisterId, String saleNumber,
        BigDecimal subtotal, BigDecimal discountAmount,
        BigDecimal taxAmount, BigDecimal totalAmount,
        String paymentMethod, String status, Instant saleDate, Long createdBy,
        List<SaleItemResponse> items
) {
    public record SaleItemResponse(
            Long id, Long productId, Integer quantity,
            BigDecimal unitPrice, BigDecimal subtotal, BigDecimal discount
    ) {}
}
