package drinks.system.reportingservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.reportingservice.infrastructure.adapter.out.persistence.entity.MonthlySalesSummaryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MonthlySalesSummaryJpaRepository extends JpaRepository<MonthlySalesSummaryEntity, Long> {
    @Query("""
            SELECT m FROM MonthlySalesSummaryEntity m
            WHERE (:branchId IS NULL OR m.branchId = :branchId)
            AND (:year IS NULL OR m.year = :year)
            """)
    Page<MonthlySalesSummaryEntity> findAllFiltered(Pageable pageable,
            @Param("branchId") Long branchId, @Param("year") Integer year);
}
