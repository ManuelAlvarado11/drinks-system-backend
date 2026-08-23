package drinks.system.salesservice.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record CashRegisterResponse(
        Long id, Long branchId, Long userId, String username,
        BigDecimal openingAmount, BigDecimal closingAmount,
        BigDecimal expectedAmount, BigDecimal difference,
        String status, Instant openedAt, Instant closedAt, String notes
) {}
