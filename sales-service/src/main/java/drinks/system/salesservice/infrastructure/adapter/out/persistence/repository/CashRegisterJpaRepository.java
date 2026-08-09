package drinks.system.salesservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.CashRegisterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface CashRegisterJpaRepository extends JpaRepository<CashRegisterEntity, Long> {

    @Query("""
            SELECT c FROM CashRegisterEntity c
            WHERE (:branchId IS NULL OR c.branchId = :branchId)
            AND (CAST(:status AS string) IS NULL OR c.status = :status)
            AND (:userId IS NULL OR c.userId = :userId)
            AND (CAST(:dateFrom AS timestamp) IS NULL OR c.openedAt >= :dateFrom)
            AND (CAST(:dateTo AS timestamp) IS NULL OR c.openedAt <= :dateTo)
            """)
    Page<CashRegisterEntity> findAllFiltered(Pageable pageable,
                                              @Param("branchId") Long branchId,
                                              @Param("status") String status,
                                              @Param("userId") Long userId,
                                              @Param("dateFrom") Instant dateFrom,
                                              @Param("dateTo") Instant dateTo);

    Optional<CashRegisterEntity> findByUserIdAndBranchIdAndStatus(Long userId, Long branchId, String status);

    Optional<CashRegisterEntity> findByUserIdAndStatus(Long userId, String status);
}
