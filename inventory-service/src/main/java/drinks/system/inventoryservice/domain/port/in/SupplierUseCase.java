package drinks.system.inventoryservice.domain.port.in;

import drinks.system.inventoryservice.application.dto.request.CreateSupplierRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateSupplierRequest;
import drinks.system.inventoryservice.application.dto.response.SupplierResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface SupplierUseCase {
    SupplierResponse create(CreateSupplierRequest request, Long userId);
    PageResponse<SupplierResponse> findAll(Pageable pageable, String search);
    SupplierResponse findById(Long id);
    SupplierResponse update(Long id, UpdateSupplierRequest request, Long userId);
    void delete(Long id);
}
