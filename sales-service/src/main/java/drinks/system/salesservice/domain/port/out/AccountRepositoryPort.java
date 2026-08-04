package drinks.system.salesservice.domain.port.out;

import drinks.system.salesservice.domain.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Optional;

public interface AccountRepositoryPort {
    Optional<Account> findById(Long id);
    Account save(Account account);
    Page<Account> findAll(Pageable pageable, Long branchId, String status, Instant dateFrom, Instant dateTo);
}
