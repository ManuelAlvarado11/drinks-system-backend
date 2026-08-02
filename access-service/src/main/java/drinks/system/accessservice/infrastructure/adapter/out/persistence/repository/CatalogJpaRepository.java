package drinks.system.accessservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.CatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogJpaRepository extends JpaRepository<CatalogEntity, Long> {

    List<CatalogEntity> findByCatalogTypeAndIsActiveTrueOrderBySortOrderAsc(String catalogType);

    boolean existsByCatalogTypeAndCode(String catalogType, String code);

    @Query("SELECT DISTINCT c.catalogType FROM CatalogEntity c")
    List<String> findDistinctCatalogTypes();
}
