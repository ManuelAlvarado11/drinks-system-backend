package drinks.system.inventoryservice.domain.port.in;

import drinks.system.inventoryservice.application.dto.request.CreateProductPresentationRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateProductPresentationRequest;
import drinks.system.inventoryservice.application.dto.response.ProductPresentationResponse;
import java.util.List;

public interface ProductPresentationUseCase {
    List<ProductPresentationResponse> findByProductId(Long productId);
    ProductPresentationResponse create(Long productId, CreateProductPresentationRequest request);
    ProductPresentationResponse update(Long id, UpdateProductPresentationRequest request);
    void delete(Long id);
}
