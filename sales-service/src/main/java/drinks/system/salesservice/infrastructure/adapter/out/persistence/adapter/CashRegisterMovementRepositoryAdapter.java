package drinks.system.salesservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.salesservice.application.mapper.CashRegisterMapper;
import drinks.system.salesservice.domain.model.CashRegisterMovement;
import drinks.system.salesservice.domain.port.out.CashRegisterMovementRepositoryPort;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.repository.CashRegisterMovementJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CashRegisterMovementRepositoryAdapter implements CashRegisterMovementRepositoryPort {
    private final CashRegisterMovementJpaRepository repo;
    private final CashRegisterMapper mapper;

    @Override
    public CashRegisterMovement save(CashRegisterMovement m) { return mapper.movementToDomain(repo.save(mapper.movementToEntity(m))); }
    @Override
    public List<CashRegisterMovement> findByCashRegisterId(Long id) {
        return repo.findByCashRegisterIdOrderByCreatedAtAsc(id).stream().map(mapper::movementToDomain).toList();
    }
    @Override
    public BigDecimal sumByTypeAndCashRegisterId(Long id, String type) { return repo.sumByTypeAndCashRegisterId(id, type); }
}
