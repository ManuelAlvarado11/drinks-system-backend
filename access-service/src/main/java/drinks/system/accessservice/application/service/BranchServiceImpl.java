package drinks.system.accessservice.application.service;

import drinks.system.accessservice.application.dto.request.BranchStatusRequest;
import drinks.system.accessservice.application.dto.request.CreateBranchRequest;
import drinks.system.accessservice.application.dto.request.UpdateBranchRequest;
import drinks.system.accessservice.application.dto.response.BranchResponse;
import drinks.system.accessservice.application.mapper.BranchMapper;
import drinks.system.accessservice.domain.model.Branch;
import drinks.system.accessservice.domain.port.in.BranchUseCase;
import drinks.system.accessservice.domain.port.out.BranchRepositoryPort;
import drinks.system.common.audit.AuditEvent;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchUseCase {

    private final BranchRepositoryPort branchRepository;
    private final BranchMapper branchMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public BranchResponse create(CreateBranchRequest request, Long currentUserId) {
        Branch branch = new Branch(
                null, request.name(), request.address(), request.phone(), request.email(),
                true, null, null, null, currentUserId, currentUserId
        );

        Branch saved = branchRepository.save(branch);

        eventPublisher.publishEvent(new AuditEvent(
                currentUserId, null, "CREATE", "ACCESS",
                "Branch", saved.id(), null, null, null,
                "Sucursal creada: " + saved.name()
        ));

        return branchMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BranchResponse> findAll(Pageable pageable, Boolean isActive) {
        Page<Branch> page = branchRepository.findAll(pageable, isActive);
        List<BranchResponse> content = page.getContent().stream()
                .map(branchMapper::toResponse)
                .toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchResponse findById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", id));
        return branchMapper.toResponse(branch);
    }

    @Override
    @Transactional
    public BranchResponse update(Long id, UpdateBranchRequest request, Long currentUserId) {
        Branch existing = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", id));

        Branch updated = new Branch(
                existing.id(),
                request.name() != null ? request.name() : existing.name(),
                request.address() != null ? request.address() : existing.address(),
                request.phone() != null ? request.phone() : existing.phone(),
                request.email() != null ? request.email() : existing.email(),
                existing.isActive(),
                existing.deletedAt(),
                existing.createdAt(),
                existing.updatedAt(),
                existing.createdBy(),
                currentUserId
        );

        Branch saved = branchRepository.save(updated);

        eventPublisher.publishEvent(new AuditEvent(
                currentUserId, null, "UPDATE", "ACCESS",
                "Branch", id, null, null, null,
                "Sucursal actualizada: " + saved.name()
        ));

        return branchMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, BranchStatusRequest request, Long currentUserId) {
        Branch existing = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", id));

        Branch updated = new Branch(
                existing.id(), existing.name(), existing.address(), existing.phone(),
                existing.email(), request.isActive(), existing.deletedAt(),
                existing.createdAt(), existing.updatedAt(), existing.createdBy(), currentUserId
        );

        branchRepository.save(updated);

        eventPublisher.publishEvent(new AuditEvent(
                currentUserId, null, "UPDATE", "ACCESS",
                "Branch", id, null, null, null,
                "Estado de sucursal actualizado: " + existing.name() + " -> " + request.isActive()
        ));
    }
}
