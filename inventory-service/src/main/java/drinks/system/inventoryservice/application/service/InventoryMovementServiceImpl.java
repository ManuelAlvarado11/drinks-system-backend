package drinks.system.inventoryservice.application.service;

import drinks.system.inventoryservice.application.dto.request.CreateMovementRequest;
import drinks.system.inventoryservice.application.dto.response.InventoryMovementResponse;
import drinks.system.inventoryservice.application.mapper.InventoryMovementMapper;
import drinks.system.inventoryservice.domain.model.InventoryMovement;
import drinks.system.inventoryservice.domain.model.ProductStock;
import drinks.system.inventoryservice.domain.port.in.InventoryMovementUseCase;
import drinks.system.inventoryservice.domain.port.out.InventoryMovementRepositoryPort;
import drinks.system.inventoryservice.domain.port.out.ProductStockRepositoryPort;
import drinks.system.common.audit.AuditEvent;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.exception.BusinessConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service @RequiredArgsConstructor
public class InventoryMovementServiceImpl implements InventoryMovementUseCase {
    private final InventoryMovementRepositoryPort movementRepository;
    private final ProductStockRepositoryPort stockRepository;
    private final InventoryMovementMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    private static final Set<String> MANUAL_TYPES = Set.of("ENTRY", "EXIT", "ADJUSTMENT");

    @Override @Transactional
    public InventoryMovementResponse create(CreateMovementRequest req, Long userId) {
        if (!MANUAL_TYPES.contains(req.movementType())) {
            throw new BusinessConflictException("Tipo de movimiento no permitido manualmente: " + req.movementType());
        }
        ProductStock stock = stockRepository.findByProductIdAndBranchId(req.productId(), req.branchId())
                .orElse(new ProductStock(null, req.productId(), req.branchId(), 0, 0, null));
        int previousStock = stock.currentStock();
        int newStock = switch (req.movementType()) {
            case "ENTRY" -> previousStock + req.quantity();
            case "EXIT" -> previousStock - req.quantity();
            case "ADJUSTMENT" -> req.quantity();
            default -> previousStock;
        };
        ProductStock updated = new ProductStock(stock.id(), stock.productId(), stock.branchId(),
                newStock, stock.minimumStock(), stock.updatedAt());
        stockRepository.save(updated);

        InventoryMovement movement = new InventoryMovement(null, req.productId(), req.branchId(),
                req.movementType(), req.quantity(), previousStock, newStock, null, null, req.notes(), null, userId);
        InventoryMovement saved = movementRepository.save(movement);
        eventPublisher.publishEvent(new AuditEvent(userId, null, "CREATE", "INVENTORY",
                "InventoryMovement", saved.id(), null, null, null,
                "Movimiento " + req.movementType() + " producto=" + req.productId() + " qty=" + req.quantity()));
        return mapper.toResponse(saved);
    }

    @Override @Transactional(readOnly = true)
    public PageResponse<InventoryMovementResponse> findAll(Pageable pageable, Long productId, Long branchId,
                                                            String type, Instant dateFrom, Instant dateTo) {
        Page<InventoryMovement> page = movementRepository.findAll(pageable, productId, branchId, type, dateFrom, dateTo);
        List<InventoryMovementResponse> content = page.getContent().stream().map(mapper::toResponse).toList();
        return PageResponse.of(page, content);
    }
}
