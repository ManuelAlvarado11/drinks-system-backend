package drinks.system.salesservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.salesservice.application.mapper.BranchPrinterMapper;
import drinks.system.salesservice.domain.model.BranchPrinter;
import drinks.system.salesservice.domain.port.out.BranchPrinterRepositoryPort;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.repository.BranchPrinterJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BranchPrinterRepositoryAdapter implements BranchPrinterRepositoryPort {

    private final BranchPrinterJpaRepository repo;
    private final BranchPrinterMapper mapper;

    @Override
    public List<BranchPrinter> findByBranchId(Long branchId) {
        return repo.findByBranchIdOrderByIsDefaultDescNameAsc(branchId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<BranchPrinter> findById(Long id) {
        return repo.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<BranchPrinter> findDefaultByBranchId(Long branchId) {
        return repo.findFirstByBranchIdAndIsDefaultTrueAndIsActiveTrue(branchId).map(mapper::toDomain);
    }

    @Override
    public BranchPrinter save(BranchPrinter printer) {
        return mapper.toDomain(repo.save(mapper.toEntity(printer)));
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    @Transactional
    public void clearDefaultForBranch(Long branchId) {
        repo.clearDefaultForBranch(branchId);
    }
}
