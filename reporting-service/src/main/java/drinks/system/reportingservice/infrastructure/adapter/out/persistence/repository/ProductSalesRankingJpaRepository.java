package drinks.system.reportingservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.reportingservice.infrastructure.adapter.out.persistence.entity.ProductSalesRankingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

public interface ProductSalesRankingJpaRepository extends JpaRepository<ProductSalesRankingEntity, Long> {
    @Query("""
            SELECT p FROM ProductSalesRankingEntity p
            WHERE (:branchId IS NULL OR p.branchId = :branchId)
            AND (CAST(:periodStart AS date) IS NULL OR p.periodStart >= :periodStart)
            AND (CAST(:periodEnd AS date) IS NULL OR p.periodEnd <= :periodEnd)
            """)
    Page<ProductSalesRankingEntity> findAllFiltered(Pageable pageable,
            @Param("branchId") Long branchId,
            @Param("periodStart") LocalDate periodStart,
            @Param("periodEnd") LocalDate periodEnd);
}
