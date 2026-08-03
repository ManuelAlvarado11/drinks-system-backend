package drinks.system.accessservice.application.service;

import drinks.system.accessservice.application.dto.request.CreateCatalogRequest;
import drinks.system.accessservice.application.dto.request.UpdateCatalogRequest;
import drinks.system.accessservice.application.dto.response.CatalogResponse;
import drinks.system.accessservice.application.mapper.CatalogMapper;
import drinks.system.accessservice.domain.model.Catalog;
import drinks.system.accessservice.domain.port.in.CatalogUseCase;
import drinks.system.accessservice.domain.port.out.CatalogRepositoryPort;
import drinks.system.common.audit.AuditEvent;
import drinks.system.common.exception.BusinessConflictException;
import drinks.system.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogUseCase {

    private final CatalogRepositoryPort catalogRepository;
    private final CatalogMapper catalogMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public CatalogResponse create(CreateCatalogRequest request) {
        if (catalogRepository.existsByTypeAndCode(request.catalogType(), request.code())) {
            throw new BusinessConflictException(
                    "Ya existe un catálogo con tipo '" + request.catalogType() + "' y código '" + request.code() + "'");
        }

        Catalog catalog = new Catalog(
                null, request.catalogType(), request.code(), request.name(),
                request.description(), request.sortOrder(), true, request.parentId(),
                null, null
        );

        Catalog saved = catalogRepository.save(catalog);

        eventPublisher.publishEvent(new AuditEvent(
                null, null, "CREATE", "ACCESS",
                "Catalog", saved.id(), null, null, null,
                "Catálogo creado: " + saved.catalogType() + "/" + saved.code()
        ));

        return catalogMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogResponse> findByType(String catalogType) {
        return catalogRepository.findByType(catalogType).stream()
                .map(catalogMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findDistinctTypes() {
        return catalogRepository.findDistinctTypes();
    }

    @Override
    @Transactional(readOnly = true)
    public CatalogResponse findById(Long id) {
        Catalog catalog = catalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catálogo", id));
        return catalogMapper.toResponse(catalog);
    }

    @Override
    @Transactional
    public CatalogResponse update(Long id, UpdateCatalogRequest request) {
        Catalog existing = catalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catálogo", id));

        Catalog updated = new Catalog(
                existing.id(),
                existing.catalogType(),
                existing.code(),
                request.name() != null ? request.name() : existing.name(),
                request.description() != null ? request.description() : existing.description(),
                request.sortOrder() != null ? request.sortOrder() : existing.sortOrder(),
                existing.isActive(),
                existing.parentId(),
                existing.createdAt(),
                existing.updatedAt()
        );

        Catalog saved = catalogRepository.save(updated);

        eventPublisher.publishEvent(new AuditEvent(
                null, null, "UPDATE", "ACCESS",
                "Catalog", id, null, null, null,
                "Catálogo actualizado: " + saved.catalogType() + "/" + saved.code()
        ));

        return catalogMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Catalog existing = catalogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catálogo", id));

        Catalog deleted = new Catalog(
                existing.id(), existing.catalogType(), existing.code(), existing.name(),
                existing.description(), existing.sortOrder(), false, existing.parentId(),
                existing.createdAt(), existing.updatedAt()
        );

        catalogRepository.save(deleted);

        eventPublisher.publishEvent(new AuditEvent(
                null, null, "DELETE", "ACCESS",
                "Catalog", id, null, null, null,
                "Catálogo desactivado: " + existing.catalogType() + "/" + existing.code()
        ));
    }
}
