package drinks.system.salesservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.salesservice.application.mapper.AccountMapper;
import drinks.system.salesservice.domain.model.AccountDetail;
import drinks.system.salesservice.domain.port.out.AccountDetailRepositoryPort;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.repository.AccountDetailJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AccountDetailRepositoryAdapter implements AccountDetailRepositoryPort {
    private final AccountDetailJpaRepository repo;
    private final AccountMapper mapper;

    @Override
    public AccountDetail save(AccountDetail d) { return mapper.detailToDomain(repo.save(mapper.detailToEntity(d))); }
    @Override
    public List<AccountDetail> findByAccountId(Long id) { return repo.findByAccountIdOrderByAddedAtAsc(id).stream().map(mapper::detailToDomain).toList(); }
    @Override
    public Optional<AccountDetail> findById(Long id) { return repo.findById(id).map(mapper::detailToDomain); }
}
