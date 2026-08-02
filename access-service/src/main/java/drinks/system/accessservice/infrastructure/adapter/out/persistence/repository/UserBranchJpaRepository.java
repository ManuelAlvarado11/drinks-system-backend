package drinks.system.accessservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.BranchEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.UserBranchEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserBranchJpaRepository extends JpaRepository<UserBranchEntity, Long> {

    List<UserBranchEntity> findByUser(UserEntity user);

    @Transactional
    void deleteByUserAndBranch(UserEntity user, BranchEntity branch);
}
