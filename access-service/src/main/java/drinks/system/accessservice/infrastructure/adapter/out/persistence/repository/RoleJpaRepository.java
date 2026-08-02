package drinks.system.accessservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {

    boolean existsByCode(String code);

    @Query("""
            SELECT r FROM RoleEntity r
            JOIN UserRoleEntity ur ON ur.role.id = r.id
            WHERE ur.user.id = :userId
            """)
    List<RoleEntity> findByUserId(@Param("userId") Long userId);
}
