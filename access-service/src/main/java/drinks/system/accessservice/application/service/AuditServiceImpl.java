package drinks.system.accessservice.application.service;

import drinks.system.accessservice.application.dto.response.AuditLogResponse;
import drinks.system.accessservice.application.mapper.AuditLogMapper;
import drinks.system.accessservice.domain.model.AuditLog;
import drinks.system.accessservice.domain.port.in.AuditUseCase;
import drinks.system.accessservice.domain.port.out.AuditLogRepositoryPort;
import drinks.system.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditUseCase {

    private final AuditLogRepositoryPort auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> findAll(Pageable pageable, Long userId, String module,
                                                   String entityName, Instant dateFrom, Instant dateTo) {
        Page<AuditLog> page = auditLogRepository.findAll(pageable, userId, module, entityName, dateFrom, dateTo);
        List<AuditLogResponse> content = page.getContent().stream()
                .map(auditLogMapper::toResponse)
                .toList();
        return PageResponse.of(page, content);
    }
}
