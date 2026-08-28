package drinks.system.inventoryservice.application.service;

import drinks.system.inventoryservice.application.dto.request.CreateCategoryRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateCategoryRequest;
import drinks.system.inventoryservice.application.dto.response.CategoryResponse;
import drinks.system.inventoryservice.application.mapper.CategoryMapper;
import drinks.system.inventoryservice.domain.model.Category;
import drinks.system.inventoryservice.domain.port.in.CategoryUseCase;
import drinks.system.inventoryservice.domain.port.out.CategoryRepositoryPort;
import drinks.system.common.audit.AuditEvent;
import drinks.system.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service @RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryUseCase {
    private final CategoryRepositoryPort categoryRepository;
    private final CategoryMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override @Transactional
    public CategoryResponse create(CreateCategoryRequest req, Long userId) {
        Category c = new Category(null, req.name(), req.description(), req.icon(), req.parentCategoryId(),
                true, null, null, null, userId, userId);
        Category saved = categoryRepository.save(c);
        eventPublisher.publishEvent(new AuditEvent(userId, null, "CREATE", "INVENTORY",
                "Category", saved.id(), null, null, null, "Categoría creada: " + saved.name()));
        return mapper.toResponse(saved);
    }

    @Override @Transactional(readOnly = true)
    public List<CategoryResponse> findAll(Boolean isActive) {
        return categoryRepository.findAll(isActive).stream().map(mapper::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return mapper.toResponse(categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id)));
    }

    @Override @Transactional
    public CategoryResponse update(Long id, UpdateCategoryRequest req, Long userId) {
        Category existing = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
        Category updated = new Category(existing.id(),
                req.name() != null ? req.name() : existing.name(),
                req.description() != null ? req.description() : existing.description(),
                req.icon() != null ? req.icon() : existing.icon(),
                req.parentCategoryId() != null ? req.parentCategoryId() : existing.parentCategoryId(),
                existing.isActive(), existing.deletedAt(), existing.createdAt(), existing.updatedAt(),
                existing.createdBy(), userId);
        Category saved = categoryRepository.save(updated);
        eventPublisher.publishEvent(new AuditEvent(userId, null, "UPDATE", "INVENTORY",
                "Category", id, null, null, null, "Categoría actualizada: " + saved.name()));
        return mapper.toResponse(saved);
    }

    @Override @Transactional
    public void delete(Long id) {
        Category existing = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
        Category deleted = new Category(existing.id(), existing.name(), existing.description(), existing.icon(),
                existing.parentCategoryId(), false, Instant.now(), existing.createdAt(), existing.updatedAt(),
                existing.createdBy(), existing.updatedBy());
        categoryRepository.save(deleted);
        eventPublisher.publishEvent(new AuditEvent(null, null, "DELETE", "INVENTORY",
                "Category", id, null, null, null, "Categoría desactivada: " + existing.name()));
    }
}
