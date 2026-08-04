package drinks.system.salesservice.domain.model;

import java.time.Instant;
import java.util.List;

public record Account(
        Long id,
        Long branchId,
        String customerName,
        String customerLastName,
        String tableNumber,
        String internalCode,
        String status,
        Instant openedAt,
        Instant closedAt,
        Long openedBy,
        Long closedBy,
        String notes,
        Instant createdAt,
        Instant updatedAt,
        List<AccountDetail> details
) {
}
