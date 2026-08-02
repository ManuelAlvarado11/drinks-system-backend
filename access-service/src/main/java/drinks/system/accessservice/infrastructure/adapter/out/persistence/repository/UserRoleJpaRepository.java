package drinks.system.accessservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.RoleEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.UserEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRoleJpaRepository extends JpaRepository<UserRoleEntity, Long> {

    List<UserRoleEntity> findByUser(UserEntity user);

    @Transactional
    void deleteByUserAndRole(UserEntity user, RoleEntity role);

    @Transactional
    void deleteAllByUser(UserEntity user);
}
