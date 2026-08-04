package drinks.system.salesservice.infrastructure.adapter.out.persistence.repository;

import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.AccountDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountDetailJpaRepository extends JpaRepository<AccountDetailEntity, Long> {

    List<AccountDetailEntity> findByAccountIdOrderByAddedAtAsc(Long accountId);
}
