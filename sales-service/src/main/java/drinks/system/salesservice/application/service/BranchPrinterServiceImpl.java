package drinks.system.salesservice.application.service;

import drinks.system.salesservice.application.dto.request.BranchPrinterRequest;
import drinks.system.salesservice.application.dto.response.BranchPrinterResponse;
import drinks.system.salesservice.application.mapper.BranchPrinterMapper;
import drinks.system.salesservice.domain.model.BranchPrinter;
import drinks.system.salesservice.domain.port.in.BranchPrinterUseCase;
import drinks.system.salesservice.domain.port.out.BranchPrinterRepositoryPort;
import drinks.system.salesservice.infrastructure.adapter.out.printer.TcpReceiptPrinterAdapter;
import drinks.system.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchPrinterServiceImpl implements BranchPrinterUseCase {

    private final BranchPrinterRepositoryPort printerRepository;
    private final BranchPrinterMapper mapper;
    private final TcpReceiptPrinterAdapter tcpPrinter;

    @Override
    @Transactional(readOnly = true)
    public List<BranchPrinterResponse> findByBranch(Long branchId) {
        return printerRepository.findByBranchId(branchId).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public BranchPrinterResponse create(BranchPrinterRequest r, Long userId) {
        boolean makeDefault = Boolean.TRUE.equals(r.isDefault());
        if (makeDefault) {
            printerRepository.clearDefaultForBranch(r.branchId());
        }
        BranchPrinter printer = new BranchPrinter(null, r.branchId(), r.name(), r.host(), r.port(),
                normalizeWidth(r.paperWidthMm()), r.copies() == null ? 1 : r.copies(),
                r.cutPaper() == null || r.cutPaper(), Boolean.TRUE.equals(r.openDrawer()),
                makeDefault, r.isActive() == null || r.isActive(), null, null, userId, userId);
        return mapper.toResponse(printerRepository.save(printer));
    }

    @Override
    @Transactional
    public BranchPrinterResponse update(Long id, BranchPrinterRequest r, Long userId) {
        BranchPrinter existing = printerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Impresora", id));
        boolean makeDefault = Boolean.TRUE.equals(r.isDefault());
        if (makeDefault && !Boolean.TRUE.equals(existing.isDefault())) {
            printerRepository.clearDefaultForBranch(existing.branchId());
        }
        BranchPrinter updated = new BranchPrinter(existing.id(), existing.branchId(), r.name(), r.host(), r.port(),
                normalizeWidth(r.paperWidthMm()), r.copies() == null ? 1 : r.copies(),
                r.cutPaper() == null || r.cutPaper(), Boolean.TRUE.equals(r.openDrawer()),
                makeDefault, r.isActive() == null || r.isActive(),
                existing.createdAt(), existing.updatedAt(), existing.createdBy(), userId);
        return mapper.toResponse(printerRepository.save(updated));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        BranchPrinter existing = printerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Impresora", id));
        printerRepository.deleteById(existing.id());
    }

    @Override
    public void test(Long id) {
        BranchPrinter printer = printerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Impresora", id));
        tcpPrinter.printTest(printer);
    }

    private Integer normalizeWidth(Integer width) {
        return (width != null && width == 58) ? 58 : 80;
    }
}
