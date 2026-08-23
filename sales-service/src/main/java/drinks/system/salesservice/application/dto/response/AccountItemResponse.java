package drinks.system.salesservice.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountItemResponse(
        Long id, Long productId, String productName, Integer quantity,
        BigDecimal unitPrice, BigDecimal subtotal,
        Instant addedAt, Long addedBy, Boolean isCancelled
) {}
