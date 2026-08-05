package drinks.system.reportingservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.reportingservice.infrastructure.adapter.out.persistence.entity.DailySalesSummaryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

public interface DailySalesSummaryJpaRepository extends JpaRepository<DailySalesSummaryEntity, Long> {
    @Query("""
            SELECT d FROM DailySalesSummaryEntity d
            WHERE (:branchId IS NULL OR d.branchId = :branchId)
            AND (:dateFrom IS NULL OR d.summaryDate >= :dateFrom)
            AND (:dateTo IS NULL OR d.summaryDate <= :dateTo)
            """)
    Page<DailySalesSummaryEntity> findAllFiltered(Pageable pageable,
            @Param("branchId") Long branchId,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo);
}
