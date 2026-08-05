package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.ProductStockEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ProductStockJpaRepository extends JpaRepository<ProductStockEntity, Long> {
    Optional<ProductStockEntity> findByProductIdAndBranchId(Long productId, Long branchId);

    @Query("""
            SELECT s FROM ProductStockEntity s
            WHERE s.branchId = :branchId
            AND (:lowStock IS NULL OR :lowStock = false OR s.currentStock <= s.minimumStock)
            """)
    Page<ProductStockEntity> findByBranchFiltered(Pageable pageable,
                                                   @Param("branchId") Long branchId,
                                                   @Param("lowStock") Boolean lowStock);
}
