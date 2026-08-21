package drinks.system.inventoryservice.application.service;

import drinks.system.inventoryservice.application.dto.request.CreateProductPresentationRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateProductPresentationRequest;
import drinks.system.inventoryservice.application.dto.response.ProductPresentationResponse;
import drinks.system.inventoryservice.application.mapper.ProductPresentationMapper;
import drinks.system.inventoryservice.domain.model.ProductPresentation;
import drinks.system.inventoryservice.domain.port.in.ProductPresentationUseCase;
import drinks.system.inventoryservice.domain.port.out.ProductPresentationRepositoryPort;
import drinks.system.inventoryservice.domain.port.out.ProductRepositoryPort;
import drinks.system.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
public class ProductPresentationServiceImpl implements ProductPresentationUseCase {
    private final ProductPresentationRepositoryPort presentationRepository;
    private final ProductRepositoryPort productRepository;
    private final ProductPresentationMapper mapper;

    @Override @Transactional(readOnly = true)
    public List<ProductPresentationResponse> findByProductId(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));
        return presentationRepository.findByProductId(productId).stream()
                .map(mapper::toResponse).toList();
    }

    @Override @Transactional
    public ProductPresentationResponse create(Long productId, CreateProductPresentationRequest request) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", productId));
        int sortOrder = request.sortOrder() != null ? request.sortOrder() : 0;
        ProductPresentation presentation = new ProductPresentation(
                null, productId, request.name(), request.quantity(),
                request.price(), true, sortOrder, null, null);
        return mapper.toResponse(presentationRepository.save(presentation));
    }

    @Override @Transactional
    public ProductPresentationResponse update(Long id, UpdateProductPresentationRequest request) {
        ProductPresentation existing = presentationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presentación", id));
        ProductPresentation updated = new ProductPresentation(
                existing.id(), existing.productId(),
                request.name() != null ? request.name() : existing.name(),
                request.quantity() != null ? request.quantity() : existing.quantity(),
                request.price() != null ? request.price() : existing.price(),
                request.isActive() != null ? request.isActive() : existing.isActive(),
                request.sortOrder() != null ? request.sortOrder() : existing.sortOrder(),
                existing.createdAt(), existing.updatedAt());
        return mapper.toResponse(presentationRepository.save(updated));
    }

    @Override @Transactional
    public void delete(Long id) {
        presentationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presentación", id));
        presentationRepository.deleteById(id);
    }
}
