package drinks.system.inventoryservice.domain.port.in;

import drinks.system.inventoryservice.application.dto.request.CreateProductRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateProductRequest;
import drinks.system.inventoryservice.application.dto.response.ProductResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ProductUseCase {
    ProductResponse create(CreateProductRequest request, Long userId);
    PageResponse<ProductResponse> findAll(Pageable pageable, Long categoryId, Boolean isActive, String search);
    ProductResponse findById(Long id);
    ProductResponse update(Long id, UpdateProductRequest request, Long userId);
    void delete(Long id);
}
