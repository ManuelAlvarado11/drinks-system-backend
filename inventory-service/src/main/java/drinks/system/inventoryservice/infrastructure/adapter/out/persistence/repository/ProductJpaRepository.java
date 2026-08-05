package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {
    boolean existsByCode(String code);

    @Query("""
            SELECT p FROM ProductEntity p
            WHERE (:categoryId IS NULL OR p.categoryId = :categoryId)
            AND (:isActive IS NULL OR p.isActive = :isActive)
            AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
                 OR p.code LIKE CONCAT('%', :search, '%'))
            """)
    Page<ProductEntity> findAllFiltered(Pageable pageable,
                                         @Param("categoryId") Long categoryId,
                                         @Param("isActive") Boolean isActive,
                                         @Param("search") String search);
}
