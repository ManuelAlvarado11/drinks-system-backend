package drinks.system.accessservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
            SELECT u FROM UserEntity u
            WHERE (:isActive IS NULL OR u.isActive = :isActive)
            AND (:branchId IS NULL OR u.branchId = :branchId)
            AND (:search IS NULL
                 OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                 OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<UserEntity> findAllFiltered(Pageable pageable,
                                     @Param("isActive") Boolean isActive,
                                     @Param("branchId") Long branchId,
                                     @Param("search") String search);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.lastLogin = :lastLogin WHERE u.id = :id")
    void updateLastLogin(@Param("id") Long id, @Param("lastLogin") Instant lastLogin);
}
