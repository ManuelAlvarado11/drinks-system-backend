package drinks.system.inventoryservice.domain.port.in;

import drinks.system.inventoryservice.application.dto.request.CreatePurchaseOrderRequest;
import drinks.system.inventoryservice.application.dto.request.ReceivePurchaseOrderRequest;
import drinks.system.inventoryservice.application.dto.response.PurchaseOrderDetailResponse;
import drinks.system.inventoryservice.application.dto.response.PurchaseOrderResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface PurchaseOrderUseCase {
    PurchaseOrderResponse create(CreatePurchaseOrderRequest request, Long userId);
    PageResponse<PurchaseOrderResponse> findAll(Pageable pageable, Long supplierId, Long branchId, String status, Instant dateFrom, Instant dateTo);
    PurchaseOrderDetailResponse findById(Long id);
    void receive(Long id, ReceivePurchaseOrderRequest request, Long userId);
    void cancel(Long id, Long userId);
}
