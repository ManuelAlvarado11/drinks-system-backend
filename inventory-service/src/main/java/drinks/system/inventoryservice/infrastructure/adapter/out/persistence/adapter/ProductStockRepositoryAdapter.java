package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.inventoryservice.domain.model.ProductStock;
import drinks.system.inventoryservice.domain.port.out.ProductStockRepositoryPort;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.ProductStockEntity;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository.ProductStockJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository @RequiredArgsConstructor
public class ProductStockRepositoryAdapter implements ProductStockRepositoryPort {
    private final ProductStockJpaRepository repo;

    @Override public Optional<ProductStock> findByProductIdAndBranchId(Long productId, Long branchId) {
        return repo.findByProductIdAndBranchId(productId, branchId).map(this::toDomain);
    }
    @Override public ProductStock save(ProductStock s) { return toDomain(repo.save(toEntity(s))); }
    @Override public Page<ProductStock> findByBranch(Pageable p, Long branchId, Boolean lowStock) {
        return repo.findByBranchFiltered(p, branchId, lowStock).map(this::toDomain);
    }

    private ProductStock toDomain(ProductStockEntity e) {
        return new ProductStock(e.getId(), e.getProductId(), e.getBranchId(),
                e.getCurrentStock(), e.getMinimumStock(), e.getUpdatedAt());
    }
    private ProductStockEntity toEntity(ProductStock d) {
        ProductStockEntity e = new ProductStockEntity();
        e.setId(d.id()); e.setProductId(d.productId()); e.setBranchId(d.branchId());
        e.setCurrentStock(d.currentStock()); e.setMinimumStock(d.minimumStock());
        return e;
    }
}
