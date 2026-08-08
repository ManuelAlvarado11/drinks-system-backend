package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.SupplierEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupplierJpaRepository extends JpaRepository<SupplierEntity, Long> {
    @Query("""
            SELECT s FROM SupplierEntity s
            WHERE s.isActive = true
            AND (:search IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                 OR s.nit LIKE CONCAT('%', CAST(:search AS string), '%'))
            """)
    Page<SupplierEntity> findAllFiltered(Pageable pageable, @Param("search") String search);
}
