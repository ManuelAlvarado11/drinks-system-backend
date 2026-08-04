package drinks.system.salesservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record CashRegisterMovement(
        Long id,
        Long cashRegisterId,
        String movementType,
        BigDecimal amount,
        String description,
        Instant createdAt,
        Long createdBy
) {
}
