package drinks.system.salesservice.domain.port.out;

import drinks.system.salesservice.domain.model.BranchPrinter;

import java.util.List;
import java.util.Optional;

public interface BranchPrinterRepositoryPort {
    List<BranchPrinter> findByBranchId(Long branchId);
    Optional<BranchPrinter> findById(Long id);
    Optional<BranchPrinter> findDefaultByBranchId(Long branchId);
    BranchPrinter save(BranchPrinter printer);
    void deleteById(Long id);
    /** Clears the default flag for all printers of a branch (used before setting a new default). */
    void clearDefaultForBranch(Long branchId);
}
