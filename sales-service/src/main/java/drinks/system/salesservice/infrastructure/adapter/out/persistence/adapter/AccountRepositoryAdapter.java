package drinks.system.salesservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.salesservice.application.mapper.AccountMapper;
import drinks.system.salesservice.domain.model.Account;
import drinks.system.salesservice.domain.port.out.AccountRepositoryPort;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.repository.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryAdapter implements AccountRepositoryPort {
    private final AccountJpaRepository repo;
    private final AccountMapper mapper;

    @Override
    public Optional<Account> findById(Long id) { return repo.findById(id).map(mapper::toDomain); }
    @Override
    public Account save(Account a) { return mapper.toDomain(repo.save(mapper.toEntity(a))); }
    @Override
    public Page<Account> findAll(Pageable p, Long branchId, String status, Instant from, Instant to) {
        return repo.findAllFiltered(p, branchId, status, from, to).map(mapper::toDomain);
    }
}
