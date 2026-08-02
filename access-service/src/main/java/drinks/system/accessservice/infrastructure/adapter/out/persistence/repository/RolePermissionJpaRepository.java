package drinks.system.accessservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.RoleEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RolePermissionJpaRepository extends JpaRepository<RolePermissionEntity, Long> {

    List<RolePermissionEntity> findByRole(RoleEntity role);

    @Transactional
    void deleteAllByRole(RoleEntity role);
}
