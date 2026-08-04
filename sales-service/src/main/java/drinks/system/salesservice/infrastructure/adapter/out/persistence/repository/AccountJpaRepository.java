package drinks.system.salesservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {

    @Query("""
            SELECT a FROM AccountEntity a
            WHERE (:branchId IS NULL OR a.branchId = :branchId)
            AND (:status IS NULL OR a.status = :status)
            AND (:dateFrom IS NULL OR a.openedAt >= :dateFrom)
            AND (:dateTo IS NULL OR a.openedAt <= :dateTo)
            """)
    Page<AccountEntity> findAllFiltered(Pageable pageable,
                                         @Param("branchId") Long branchId,
                                         @Param("status") String status,
                                         @Param("dateFrom") Instant dateFrom,
                                         @Param("dateTo") Instant dateTo);
}
