package drinks.system.salesservice.domain.port.in;

import drinks.system.salesservice.application.dto.request.BranchPrinterRequest;
import drinks.system.salesservice.application.dto.response.BranchPrinterResponse;

import java.util.List;

public interface BranchPrinterUseCase {
    List<BranchPrinterResponse> findByBranch(Long branchId);
    BranchPrinterResponse create(BranchPrinterRequest request, Long userId);
    BranchPrinterResponse update(Long id, BranchPrinterRequest request, Long userId);
    void delete(Long id);
    /** Sends a small test ticket to the given printer to verify connectivity. */
    void test(Long id);
}
