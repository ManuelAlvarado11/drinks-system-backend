package drinks.system.accessservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.AuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, Long> {

    @Query("SELECT a FROM AuditLogEntity a WHERE " +
            "(:userId IS NULL OR a.userId = :userId) AND " +
            "(:module IS NULL OR a.module = :module) AND " +
            "(:entityName IS NULL OR a.entityName = :entityName) AND " +
            "(:dateFrom IS NULL OR a.createdAt >= :dateFrom) AND " +
            "(:dateTo IS NULL OR a.createdAt <= :dateTo)")
    Page<AuditLogEntity> findAllFiltered(Pageable pageable,
                                          @Param("userId") Long userId,
                                          @Param("module") String module,
                                          @Param("entityName") String entityName,
                                          @Param("dateFrom") Instant dateFrom,
                                          @Param("dateTo") Instant dateTo);
}
