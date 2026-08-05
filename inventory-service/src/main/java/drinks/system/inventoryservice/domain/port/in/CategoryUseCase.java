package drinks.system.inventoryservice.domain.port.in;

import drinks.system.inventoryservice.application.dto.request.CreateCategoryRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateCategoryRequest;
import drinks.system.inventoryservice.application.dto.response.CategoryResponse;
import drinks.system.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryUseCase {
    CategoryResponse create(CreateCategoryRequest request, Long userId);
    List<CategoryResponse> findAll(Boolean isActive);
    CategoryResponse findById(Long id);
    CategoryResponse update(Long id, UpdateCategoryRequest request, Long userId);
    void delete(Long id);
}
