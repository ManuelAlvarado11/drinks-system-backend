package drinks.system.accessservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.SystemMenuOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemMenuOptionJpaRepository extends JpaRepository<SystemMenuOptionEntity, Long> {

    List<SystemMenuOptionEntity> findAllByIsActiveTrue();

    @Query("SELECT m FROM SystemMenuOptionEntity m WHERE m.isActive = true AND m.permissionId IN :permissionIds")
    List<SystemMenuOptionEntity> findActiveByPermissionIds(@Param("permissionIds") List<Long> permissionIds);

    List<SystemMenuOptionEntity> findAllByIsActiveTrueAndPermissionIdIsNull();
}
