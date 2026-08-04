package drinks.system.salesservice.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record CashRegisterMovementResponse(
        Long id, Long cashRegisterId, String movementType,
        BigDecimal amount, String description, Instant createdAt, Long createdBy
) {}
