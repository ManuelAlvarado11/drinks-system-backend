package drinks.system.reportingservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.reportingservice.infrastructure.adapter.out.persistence.entity.InventoryStatusEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryStatusJpaRepository extends JpaRepository<InventoryStatusEntity, Long> {
    @Query("""
            SELECT i FROM InventoryStatusEntity i
            WHERE (:branchId IS NULL OR i.branchId = :branchId)
            AND (:lowStockOnly IS NULL OR :lowStockOnly = false OR i.isLowStock = true)
            """)
    Page<InventoryStatusEntity> findAllFiltered(Pageable pageable,
            @Param("branchId") Long branchId,
            @Param("lowStockOnly") Boolean lowStockOnly);
}
