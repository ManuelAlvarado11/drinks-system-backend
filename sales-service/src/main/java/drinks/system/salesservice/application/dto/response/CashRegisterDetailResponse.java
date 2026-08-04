package drinks.system.salesservice.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CashRegisterDetailResponse(
        Long id, Long branchId, Long userId,
        BigDecimal openingAmount, BigDecimal closingAmount,
        BigDecimal expectedAmount, BigDecimal difference,
        String status, Instant openedAt, Instant closedAt, String notes,
        List<CashRegisterMovementResponse> movements
) {}
