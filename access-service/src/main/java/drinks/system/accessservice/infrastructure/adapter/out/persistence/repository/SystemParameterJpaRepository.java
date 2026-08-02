package drinks.system.accessservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.SystemParameterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemParameterJpaRepository extends JpaRepository<SystemParameterEntity, Long> {

    Optional<SystemParameterEntity> findByParameterKey(String parameterKey);

    boolean existsByParameterKey(String parameterKey);

    @Query("SELECT p FROM SystemParameterEntity p WHERE " +
            "(:module IS NULL OR p.module = :module) AND " +
            "(:isActive IS NULL OR p.isActive = :isActive)")
    Page<SystemParameterEntity> findAllFiltered(Pageable pageable,
                                                 @Param("module") String module,
                                                 @Param("isActive") Boolean isActive);
}
