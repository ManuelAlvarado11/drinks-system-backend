package drinks.system.salesservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record CashRegister(
        Long id,
        Long branchId,
        Long userId,
        BigDecimal openingAmount,
        BigDecimal closingAmount,
        BigDecimal expectedAmount,
        BigDecimal difference,
        String status,
        Instant openedAt,
        Instant closedAt,
        String notes,
        Instant createdAt,
        Instant updatedAt,
        Long createdBy,
        Long updatedBy
) {
}
