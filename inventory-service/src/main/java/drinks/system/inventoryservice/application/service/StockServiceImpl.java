package drinks.system.inventoryservice.application.service;

import drinks.system.inventoryservice.application.dto.request.StockDeductRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateStockConfigRequest;
import drinks.system.inventoryservice.application.dto.response.ProductStockResponse;
import drinks.system.inventoryservice.domain.model.Product;
import drinks.system.inventoryservice.domain.model.InventoryMovement;
import drinks.system.inventoryservice.domain.model.ProductStock;
import drinks.system.inventoryservice.domain.port.in.StockUseCase;
import drinks.system.inventoryservice.domain.port.out.InventoryMovementRepositoryPort;
import drinks.system.inventoryservice.domain.port.out.ProductRepositoryPort;
import drinks.system.inventoryservice.domain.port.out.ProductStockRepositoryPort;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
public class StockServiceImpl implements StockUseCase {
    private final ProductStockRepositoryPort stockRepository;
    private final InventoryMovementRepositoryPort movementRepository;
    private final ProductRepositoryPort productRepository;

    @Override @Transactional(readOnly = true)
    public PageResponse<ProductStockResponse> findByBranch(Pageable pageable, Long branchId, Boolean lowStock) {
        Page<ProductStock> page = stockRepository.findByBranch(pageable, branchId, lowStock);
        List<ProductStockResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override @Transactional(readOnly = true)
    public ProductStockResponse findByProductAndBranch(Long productId, Long branchId) {
        ProductStock stock = stockRepository.findByProductIdAndBranchId(productId, branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock no encontrado para producto " + productId + " en sucursal " + branchId, productId));
        return toResponse(stock);
    }

    @Override @Transactional
    public void updateConfig(Long productId, Long branchId, UpdateStockConfigRequest request) {
        ProductStock stock = stockRepository.findByProductIdAndBranchId(productId, branchId)
                .orElse(new ProductStock(null, productId, branchId, 0, request.minimumStock(), null));
        ProductStock updated = new ProductStock(stock.id(), stock.productId(), stock.branchId(),
                stock.currentStock(), request.minimumStock(), stock.updatedAt());
        stockRepository.save(updated);
    }

    @Override @Transactional
    public void deductStock(StockDeductRequest request, Long userId) {
        for (StockDeductRequest.StockItem item : request.items()) {
            // Skip products that don't track inventory (food, shots, etc.)
            Product product = productRepository.findById(item.productId()).orElse(null);
            if (product == null || !Boolean.TRUE.equals(product.tracksInventory())) {
                continue;
            }
            ProductStock stock = stockRepository.findByProductIdAndBranchId(item.productId(), request.branchId())
                    .orElse(new ProductStock(null, item.productId(), request.branchId(), 0, 0, null));
            int previousStock = stock.currentStock();
            int newStock = previousStock - item.quantity();
            ProductStock updated = new ProductStock(stock.id(), stock.productId(), stock.branchId(),
                    newStock, stock.minimumStock(), stock.updatedAt());
            stockRepository.save(updated);
            InventoryMovement movement = new InventoryMovement(null, item.productId(), request.branchId(),
                    "SALE", item.quantity(), previousStock, newStock, "SALE", null, null, null, userId);
            movementRepository.save(movement);
        }
    }

    @Override @Transactional
    public void addStock(StockDeductRequest request, Long userId) {
        for (StockDeductRequest.StockItem item : request.items()) {
            // Skip products that don't track inventory (food, shots, etc.)
            Product product = productRepository.findById(item.productId()).orElse(null);
            if (product == null || !Boolean.TRUE.equals(product.tracksInventory())) {
                continue;
            }
            ProductStock stock = stockRepository.findByProductIdAndBranchId(item.productId(), request.branchId())
                    .orElse(new ProductStock(null, item.productId(), request.branchId(), 0, 0, null));
            int previousStock = stock.currentStock();
            int newStock = previousStock + item.quantity();
            ProductStock updated = new ProductStock(stock.id(), stock.productId(), stock.branchId(),
                    newStock, stock.minimumStock(), stock.updatedAt());
            stockRepository.save(updated);
            InventoryMovement movement = new InventoryMovement(null, item.productId(), request.branchId(),
                    "ENTRY", item.quantity(), previousStock, newStock, "SALE_CANCEL", null, null, null, userId);
            movementRepository.save(movement);
        }
    }

    private ProductStockResponse toResponse(ProductStock s) {
        boolean isLow = s.currentStock() <= s.minimumStock();
        return new ProductStockResponse(s.id(), s.productId(), s.branchId(),
                s.currentStock(), s.minimumStock(), isLow, s.updatedAt());
    }
}
