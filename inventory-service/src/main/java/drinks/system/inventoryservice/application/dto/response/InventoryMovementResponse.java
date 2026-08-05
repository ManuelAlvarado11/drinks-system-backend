package drinks.system.inventoryservice.application.dto.response;

import java.time.Instant;

public record InventoryMovementResponse(
        Long id, Long productId, Long branchId, String movementType,
        Integer quantity, Integer previousStock, Integer newStock,
        String referenceType, Long referenceId, String notes,
        Instant createdAt, Long createdBy
) {}
