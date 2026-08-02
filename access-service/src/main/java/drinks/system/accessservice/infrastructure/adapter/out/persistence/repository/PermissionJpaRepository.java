package drinks.system.accessservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, Long> {

    List<PermissionEntity> findAllByIsActiveTrue();

    @Query("""
            SELECT DISTINCT p FROM PermissionEntity p
            JOIN RolePermissionEntity rp ON rp.permission.id = p.id
            WHERE rp.role.id IN :roleIds AND p.isActive = true
            """)
    List<PermissionEntity> findByRoleIds(@Param("roleIds") List<Long> roleIds);

    List<PermissionEntity> findAllByIdIn(List<Long> ids);
}
