package drinks.system.salesservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.salesservice.application.mapper.CashRegisterMapper;
import drinks.system.salesservice.domain.model.CashRegister;
import drinks.system.salesservice.domain.port.out.CashRegisterRepositoryPort;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.repository.CashRegisterJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CashRegisterRepositoryAdapter implements CashRegisterRepositoryPort {
    private final CashRegisterJpaRepository repo;
    private final CashRegisterMapper mapper;

    @Override
    public Optional<CashRegister> findById(Long id) { return repo.findById(id).map(mapper::toDomain); }
    @Override
    public CashRegister save(CashRegister cr) { return mapper.toDomain(repo.save(mapper.toEntity(cr))); }
    @Override
    public Page<CashRegister> findAll(Pageable p, Long branchId, String status, Long userId, Instant from, Instant to) {
        return repo.findAllFiltered(p, branchId, status, userId, from, to).map(mapper::toDomain);
    }
    @Override
    public Optional<CashRegister> findOpenByUserIdAndBranchId(Long userId, Long branchId) {
        return repo.findByUserIdAndBranchIdAndStatus(userId, branchId, "OPEN").map(mapper::toDomain);
    }
    @Override
    public Optional<CashRegister> findOpenByUserId(Long userId) {
        return repo.findByUserIdAndStatus(userId, "OPEN").map(mapper::toDomain);
    }
}
