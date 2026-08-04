package drinks.system.salesservice.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountDetail(
        Long id,
        Long accountId,
        Long productId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        Instant addedAt,
        Long addedBy,
        Boolean isCancelled
) {
}
