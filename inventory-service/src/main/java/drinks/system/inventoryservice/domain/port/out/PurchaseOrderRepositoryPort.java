package drinks.system.inventoryservice.domain.port.out;

import drinks.system.inventoryservice.domain.model.PurchaseOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.Optional;

public interface PurchaseOrderRepositoryPort {
    Optional<PurchaseOrder> findById(Long id);
    PurchaseOrder save(PurchaseOrder order);
    Page<PurchaseOrder> findAll(Pageable pageable, Long supplierId, Long branchId, String status, Instant dateFrom, Instant dateTo);
    String generateOrderNumber();
}
