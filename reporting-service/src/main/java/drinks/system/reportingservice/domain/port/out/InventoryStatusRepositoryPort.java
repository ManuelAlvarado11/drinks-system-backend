package drinks.system.reportingservice.domain.port.out;

import drinks.system.reportingservice.domain.model.InventoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryStatusRepositoryPort {
    Page<InventoryStatus> findAll(Pageable pageable, Long branchId, Boolean lowStockOnly);
}
