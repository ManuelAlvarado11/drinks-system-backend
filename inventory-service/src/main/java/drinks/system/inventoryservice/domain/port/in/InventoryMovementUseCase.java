package drinks.system.inventoryservice.domain.port.in;

import drinks.system.inventoryservice.application.dto.request.CreateMovementRequest;
import drinks.system.inventoryservice.application.dto.response.InventoryMovementResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface InventoryMovementUseCase {
    InventoryMovementResponse create(CreateMovementRequest request, Long userId);
    PageResponse<InventoryMovementResponse> findAll(Pageable pageable, Long productId, Long branchId, String type, Instant dateFrom, Instant dateTo);
}
