package drinks.system.accessservice.application.service;

import drinks.system.accessservice.application.dto.request.CreateSystemParameterRequest;
import drinks.system.accessservice.application.dto.request.UpdateSystemParameterRequest;
import drinks.system.accessservice.application.dto.response.SystemParameterResponse;
import drinks.system.accessservice.application.mapper.SystemParameterMapper;
import drinks.system.accessservice.domain.model.SystemParameter;
import drinks.system.accessservice.domain.port.in.SystemParameterUseCase;
import drinks.system.accessservice.domain.port.out.SystemParameterRepositoryPort;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemParameterServiceImpl implements SystemParameterUseCase {

    private final SystemParameterRepositoryPort systemParameterRepository;
    private final SystemParameterMapper systemParameterMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public SystemParameterResponse create(CreateSystemParameterRequest request, Long currentUserId) {
        if (systemParameterRepository.existsByKey(request.parameterKey())) {
            throw new BusinessConflictException("La clave de parámetro ya existe: " + request.parameterKey());
        }

        SystemParameter param = new SystemParameter(
                null, request.parameterKey(), request.parameterValue(), request.dataType(),
                request.description(), request.module(), true,
                null, null, currentUserId, currentUserId
        );

        SystemParameter saved = systemParameterRepository.save(param);

        eventPublisher.publishEvent(new AuditEvent(
                currentUserId, null, "CREATE", "ACCESS",
                "SystemParameter", saved.id(), null, null, null,
                "Parámetro creado: " + saved.parameterKey()
        ));

        return systemParameterMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<SystemParameterResponse> findAll(Pageable pageable, String module, Boolean isActive) {
        Page<SystemParameter> page = systemParameterRepository.findAll(pageable, module, isActive);
        List<SystemParameterResponse> content = page.getContent().stream()
                .map(systemParameterMapper::toResponse)
                .toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public SystemParameterResponse findById(Long id) {
        SystemParameter param = systemParameterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parámetro", id));
        return systemParameterMapper.toResponse(param);
    }

    @Override
    @Transactional(readOnly = true)
    public SystemParameterResponse findByKey(String key) {
        SystemParameter param = systemParameterRepository.findByKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Parámetro con clave: " + key, key));
        return systemParameterMapper.toResponse(param);
    }

    @Override
    @Transactional
    public SystemParameterResponse update(Long id, UpdateSystemParameterRequest request, Long currentUserId) {
        SystemParameter existing = systemParameterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parámetro", id));

        SystemParameter updated = new SystemParameter(
                existing.id(),
                existing.parameterKey(),
                request.parameterValue() != null ? request.parameterValue() : existing.parameterValue(),
                existing.dataType(),
                request.description() != null ? request.description() : existing.description(),
                existing.module(),
                existing.isActive(),
                existing.createdAt(),
                existing.updatedAt(),
                existing.createdBy(),
                currentUserId
        );

        SystemParameter saved = systemParameterRepository.save(updated);

        eventPublisher.publishEvent(new AuditEvent(
                currentUserId, null, "UPDATE", "ACCESS",
                "SystemParameter", id, null, null, null,
                "Parámetro actualizado: " + saved.parameterKey()
        ));

        return systemParameterMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SystemParameter existing = systemParameterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parámetro", id));

        SystemParameter deleted = new SystemParameter(
                existing.id(), existing.parameterKey(), existing.parameterValue(),
                existing.dataType(), existing.description(), existing.module(),
                false, existing.createdAt(), existing.updatedAt(),
                existing.createdBy(), existing.updatedBy()
        );

        systemParameterRepository.save(deleted);

        eventPublisher.publishEvent(new AuditEvent(
                null, null, "DELETE", "ACCESS",
                "SystemParameter", id, null, null, null,
                "Parámetro desactivado: " + existing.parameterKey()
        ));
    }
}
