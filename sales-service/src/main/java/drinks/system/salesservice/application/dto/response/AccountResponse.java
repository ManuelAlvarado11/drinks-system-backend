package drinks.system.salesservice.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        Long id, Long branchId, String customerName, String customerLastName,
        String tableNumber, String internalCode, String status,
        Instant openedAt, Instant closedAt, Long openedBy, Long closedBy,
        String notes, BigDecimal total
) {}
