package drinks.system.salesservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.SaleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface SaleJpaRepository extends JpaRepository<SaleEntity, Long> {

    @Query("""
            SELECT s FROM SaleEntity s
            WHERE (:branchId IS NULL OR s.branchId = :branchId)
            AND (CAST(:status AS string) IS NULL OR s.status = :status)
            AND (CAST(:dateFrom AS timestamp) IS NULL OR s.saleDate >= :dateFrom)
            AND (CAST(:dateTo AS timestamp) IS NULL OR s.saleDate <= :dateTo)
            AND (:customerId IS NULL OR s.customerId = :customerId)
            AND (CAST(:paymentMethod AS string) IS NULL OR s.paymentMethod = :paymentMethod)
            """)
    Page<SaleEntity> findAllFiltered(Pageable pageable,
                                      @Param("branchId") Long branchId,
                                      @Param("status") String status,
                                      @Param("dateFrom") Instant dateFrom,
                                      @Param("dateTo") Instant dateTo,
                                      @Param("customerId") Long customerId,
                                      @Param("paymentMethod") String paymentMethod);

    @Query("SELECT COUNT(s) FROM SaleEntity s WHERE s.branchId = :branchId AND s.saleDate >= :startOfDay AND s.saleDate < :endOfDay")
    long countByBranchAndDate(@Param("branchId") Long branchId,
                              @Param("startOfDay") Instant startOfDay,
                              @Param("endOfDay") Instant endOfDay);
}
