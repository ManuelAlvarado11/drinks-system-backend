package drinks.system.salesservice.domain.port.out;

import drinks.system.salesservice.domain.model.AccountDetail;

import java.util.List;
import java.util.Optional;

public interface AccountDetailRepositoryPort {
    AccountDetail save(AccountDetail detail);
    List<AccountDetail> findByAccountId(Long accountId);
    Optional<AccountDetail> findById(Long id);
}
