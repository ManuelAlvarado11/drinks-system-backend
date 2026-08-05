package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.PurchaseOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;

public interface PurchaseOrderJpaRepository extends JpaRepository<PurchaseOrderEntity, Long> {
    @Query("""
            SELECT o FROM PurchaseOrderEntity o
            WHERE (:supplierId IS NULL OR o.supplierId = :supplierId)
            AND (:branchId IS NULL OR o.branchId = :branchId)
            AND (:status IS NULL OR o.status = :status)
            AND (:dateFrom IS NULL OR o.orderDate >= :dateFrom)
            AND (:dateTo IS NULL OR o.orderDate <= :dateTo)
            """)
    Page<PurchaseOrderEntity> findAllFiltered(Pageable pageable,
                                               @Param("supplierId") Long supplierId,
                                               @Param("branchId") Long branchId,
                                               @Param("status") String status,
                                               @Param("dateFrom") Instant dateFrom,
                                               @Param("dateTo") Instant dateTo);

    long count();
}
