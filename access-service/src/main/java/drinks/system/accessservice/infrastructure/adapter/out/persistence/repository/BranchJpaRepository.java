package drinks.system.accessservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.BranchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BranchJpaRepository extends JpaRepository<BranchEntity, Long> {

    @Query("""
            SELECT b FROM BranchEntity b
            WHERE (:isActive IS NULL OR b.isActive = :isActive)
            """)
    Page<BranchEntity> findAllByIsActiveFiltered(@Param("isActive") Boolean isActive, Pageable pageable);
}
