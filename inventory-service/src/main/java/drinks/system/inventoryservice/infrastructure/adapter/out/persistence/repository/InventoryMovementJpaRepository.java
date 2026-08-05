package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.InventoryMovementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;

public interface InventoryMovementJpaRepository extends JpaRepository<InventoryMovementEntity, Long> {
    @Query("""
            SELECT m FROM InventoryMovementEntity m
            WHERE (:productId IS NULL OR m.productId = :productId)
            AND (:branchId IS NULL OR m.branchId = :branchId)
            AND (:type IS NULL OR m.movementType = :type)
            AND (:dateFrom IS NULL OR m.createdAt >= :dateFrom)
            AND (:dateTo IS NULL OR m.createdAt <= :dateTo)
            """)
    Page<InventoryMovementEntity> findAllFiltered(Pageable pageable,
                                                   @Param("productId") Long productId,
                                                   @Param("branchId") Long branchId,
                                                   @Param("type") String type,
                                                   @Param("dateFrom") Instant dateFrom,
                                                   @Param("dateTo") Instant dateTo);
}
