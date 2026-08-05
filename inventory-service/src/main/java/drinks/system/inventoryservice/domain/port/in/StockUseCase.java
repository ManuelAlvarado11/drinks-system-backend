package drinks.system.inventoryservice.domain.port.in;

import drinks.system.inventoryservice.application.dto.request.StockDeductRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateStockConfigRequest;
import drinks.system.inventoryservice.application.dto.response.ProductStockResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockUseCase {
    PageResponse<ProductStockResponse> findByBranch(Pageable pageable, Long branchId, Boolean lowStock);
    ProductStockResponse findByProductAndBranch(Long productId, Long branchId);
    void updateConfig(Long productId, Long branchId, UpdateStockConfigRequest request);
    void deductStock(StockDeductRequest request, Long userId);
    void addStock(StockDeductRequest request, Long userId);
}
