package drinks.system.inventoryservice.application.service;

import drinks.system.inventoryservice.application.dto.request.CreateProductRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateProductRequest;
import drinks.system.inventoryservice.application.dto.response.ProductResponse;
import drinks.system.inventoryservice.application.mapper.ProductMapper;
import drinks.system.inventoryservice.domain.model.Product;
import drinks.system.inventoryservice.domain.port.in.ProductUseCase;
import drinks.system.inventoryservice.domain.port.out.ProductRepositoryPort;
import drinks.system.common.audit.AuditEvent;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.exception.BusinessConflictException;
import drinks.system.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service @RequiredArgsConstructor
public class ProductServiceImpl implements ProductUseCase {
    private final ProductRepositoryPort productRepository;
    private final ProductMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override @Transactional
    public ProductResponse create(CreateProductRequest req, Long userId) {
        if (productRepository.existsByCode(req.code())) {
            throw new BusinessConflictException("El código de producto ya existe: " + req.code());
        }
        Boolean tracksInventory = req.tracksInventory() != null ? req.tracksInventory() : true;
        Product p = new Product(null, req.code(), req.name(), req.categoryId(), req.size(),
                req.description(), req.costPrice(), req.salePrice(), tracksInventory,
                true, null, null, null, userId, userId);
        Product saved = productRepository.save(p);
        eventPublisher.publishEvent(new AuditEvent(userId, null, "CREATE", "INVENTORY",
                "Product", saved.id(), null, null, null, "Producto creado: " + saved.code()));
        return mapper.toResponse(saved);
    }

    @Override @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findAll(Pageable pageable, Long categoryId, Boolean isActive, String search) {
        Page<Product> page = productRepository.findAll(pageable, categoryId, isActive, search);
        List<ProductResponse> content = page.getContent().stream().map(mapper::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return mapper.toResponse(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id)));
    }

    @Override @Transactional
    public ProductResponse update(Long id, UpdateProductRequest req, Long userId) {
        Product existing = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        Product updated = new Product(existing.id(), existing.code(),
                req.name() != null ? req.name() : existing.name(),
                req.categoryId() != null ? req.categoryId() : existing.categoryId(),
                req.size() != null ? req.size() : existing.size(),
                req.description() != null ? req.description() : existing.description(),
                req.costPrice() != null ? req.costPrice() : existing.costPrice(),
                req.salePrice() != null ? req.salePrice() : existing.salePrice(),
                req.tracksInventory() != null ? req.tracksInventory() : existing.tracksInventory(),
                existing.isActive(), existing.deletedAt(), existing.createdAt(), existing.updatedAt(),
                existing.createdBy(), userId);
        Product saved = productRepository.save(updated);
        eventPublisher.publishEvent(new AuditEvent(userId, null, "UPDATE", "INVENTORY",
                "Product", id, null, null, null, "Producto actualizado: " + saved.code()));
        return mapper.toResponse(saved);
    }

    @Override @Transactional
    public void delete(Long id) {
        Product existing = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto", id));
        Product deleted = new Product(existing.id(), existing.code(), existing.name(), existing.categoryId(),
                existing.size(), existing.description(), existing.costPrice(), existing.salePrice(),
                existing.tracksInventory(), false, Instant.now(), existing.createdAt(), existing.updatedAt(),
                existing.createdBy(), existing.updatedBy());
        productRepository.save(deleted);
        eventPublisher.publishEvent(new AuditEvent(null, null, "DELETE", "INVENTORY",
                "Product", id, null, null, null, "Producto desactivado: " + existing.code()));
    }
}
