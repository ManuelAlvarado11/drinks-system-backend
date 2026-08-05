package drinks.system.inventoryservice.domain.port.out;

import drinks.system.inventoryservice.domain.model.InventoryMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.Instant;

public interface InventoryMovementRepositoryPort {
    InventoryMovement save(InventoryMovement movement);
    Page<InventoryMovement> findAll(Pageable pageable, Long productId, Long branchId, String type, Instant dateFrom, Instant dateTo);
}
