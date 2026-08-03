package drinks.system.accessservice.infrastructure.adapter.in.rest;

import drinks.system.accessservice.application.dto.response.AuditLogResponse;
import drinks.system.accessservice.domain.port.in.AuditUseCase;
import drinks.system.common.dto.ApiResponse;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.security.RequiresPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/access/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditUseCase auditUseCase;

    @GetMapping
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) Instant dateFrom,
            @RequestParam(required = false) Instant dateTo,
            @RequestParam(defaultValue = "createdAt") String sort) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 100),
                Sort.by(Sort.Direction.DESC, sort));
        PageResponse<AuditLogResponse> response = auditUseCase.findAll(
                pageable, userId, module, entityName, dateFrom, dateTo);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
