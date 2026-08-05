package drinks.system.inventoryservice.application.service;

import drinks.system.inventoryservice.application.dto.request.CreateSupplierRequest;
import drinks.system.inventoryservice.application.dto.request.UpdateSupplierRequest;
import drinks.system.inventoryservice.application.dto.response.SupplierResponse;
import drinks.system.inventoryservice.application.mapper.SupplierMapper;
import drinks.system.inventoryservice.domain.model.Supplier;
import drinks.system.inventoryservice.domain.port.in.SupplierUseCase;
import drinks.system.inventoryservice.domain.port.out.SupplierRepositoryPort;
import drinks.system.common.audit.AuditEvent;
import drinks.system.common.dto.PageResponse;
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
public class SupplierServiceImpl implements SupplierUseCase {
    private final SupplierRepositoryPort supplierRepository;
    private final SupplierMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override @Transactional
    public SupplierResponse create(CreateSupplierRequest req, Long userId) {
        Supplier s = new Supplier(null, req.name(), req.contactName(), req.phone(),
                req.email(), req.address(), req.nit(), true, null, null, null, userId, userId);
        Supplier saved = supplierRepository.save(s);
        eventPublisher.publishEvent(new AuditEvent(userId, null, "CREATE", "INVENTORY",
                "Supplier", saved.id(), null, null, null, "Proveedor creado: " + saved.name()));
        return mapper.toResponse(saved);
    }

    @Override @Transactional(readOnly = true)
    public PageResponse<SupplierResponse> findAll(Pageable pageable, String search) {
        Page<Supplier> page = supplierRepository.findAll(pageable, search);
        List<SupplierResponse> content = page.getContent().stream().map(mapper::toResponse).toList();
        return PageResponse.of(page, content);
    }

    @Override @Transactional(readOnly = true)
    public SupplierResponse findById(Long id) {
        return mapper.toResponse(supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id)));
    }

    @Override @Transactional
    public SupplierResponse update(Long id, UpdateSupplierRequest req, Long userId) {
        Supplier existing = supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
        Supplier updated = new Supplier(existing.id(),
                req.name() != null ? req.name() : existing.name(),
                req.contactName() != null ? req.contactName() : existing.contactName(),
                req.phone() != null ? req.phone() : existing.phone(),
                req.email() != null ? req.email() : existing.email(),
                req.address() != null ? req.address() : existing.address(),
                req.nit() != null ? req.nit() : existing.nit(),
                existing.isActive(), existing.deletedAt(), existing.createdAt(), existing.updatedAt(),
                existing.createdBy(), userId);
        Supplier saved = supplierRepository.save(updated);
        eventPublisher.publishEvent(new AuditEvent(userId, null, "UPDATE", "INVENTORY",
                "Supplier", id, null, null, null, "Proveedor actualizado: " + saved.name()));
        return mapper.toResponse(saved);
    }

    @Override @Transactional
    public void delete(Long id) {
        Supplier existing = supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
        Supplier deleted = new Supplier(existing.id(), existing.name(), existing.contactName(),
                existing.phone(), existing.email(), existing.address(), existing.nit(),
                false, Instant.now(), existing.createdAt(), existing.updatedAt(), existing.createdBy(), existing.updatedBy());
        supplierRepository.save(deleted);
        eventPublisher.publishEvent(new AuditEvent(null, null, "DELETE", "INVENTORY",
                "Supplier", id, null, null, null, "Proveedor desactivado: " + existing.name()));
    }
}
