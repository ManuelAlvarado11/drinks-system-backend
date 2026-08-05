package drinks.system.inventoryservice.domain.port.out;

import drinks.system.inventoryservice.domain.model.ProductStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ProductStockRepositoryPort {
    Optional<ProductStock> findByProductIdAndBranchId(Long productId, Long branchId);
    ProductStock save(ProductStock stock);
    Page<ProductStock> findByBranch(Pageable pageable, Long branchId, Boolean lowStock);
}
