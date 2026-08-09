package drinks.system.salesservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {

    boolean existsByNitCi(String nitCi);

    @Query("""
            SELECT c FROM CustomerEntity c
            WHERE c.isActive = true
            AND (:search IS NULL
                 OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                 OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))
                 OR c.nitCi LIKE CONCAT('%', CAST(:search AS string), '%'))
            """)
    Page<CustomerEntity> findAllFiltered(Pageable pageable, @Param("search") String search);
}
