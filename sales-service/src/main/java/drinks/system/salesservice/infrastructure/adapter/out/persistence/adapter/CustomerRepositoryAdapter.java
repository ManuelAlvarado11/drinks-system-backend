package drinks.system.salesservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.salesservice.application.mapper.CustomerMapper;
import drinks.system.salesservice.domain.model.Customer;
import drinks.system.salesservice.domain.port.out.CustomerRepositoryPort;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepositoryPort {
    private final CustomerJpaRepository repo;
    private final CustomerMapper mapper;

    @Override
    public Optional<Customer> findById(Long id) { return repo.findById(id).map(mapper::toDomain); }
    @Override
    public Customer save(Customer c) { return mapper.toDomain(repo.save(mapper.toEntity(c))); }
    @Override
    public Page<Customer> findAll(Pageable p, String search) { return repo.findAllFiltered(p, search).map(mapper::toDomain); }
    @Override
    public boolean existsByNitCi(String nitCi) { return repo.existsByNitCi(nitCi); }
}
