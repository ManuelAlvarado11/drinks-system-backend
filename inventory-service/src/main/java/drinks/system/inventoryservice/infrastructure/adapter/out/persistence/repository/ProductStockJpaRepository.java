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

    @Query(value = """
            SELECT s.* FROM inventory.product_stock s
            JOIN inventory.products p ON p.id = s.product_id
            WHERE s.branch_id = :branchId
            AND p.tracks_inventory = true
            AND (:lowStock IS NULL OR CAST(:lowStock AS boolean) = false OR s.current_stock <= s.minimum_stock)
            """, nativeQuery = true)
    Page<ProductStockEntity> findByBranchFiltered(Pageable pageable,
                                                   @Param("branchId") Long branchId,
                                                   @Param("lowStock") Boolean lowStock);
}
