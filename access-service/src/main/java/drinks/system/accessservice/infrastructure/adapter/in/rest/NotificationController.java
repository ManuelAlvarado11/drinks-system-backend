package drinks.system.accessservice.infrastructure.adapter.in.rest;

import drinks.system.accessservice.application.dto.request.CreateNotificationRequest;
import drinks.system.accessservice.application.dto.response.NotificationResponse;
import drinks.system.accessservice.application.dto.response.UnreadCountResponse;
import drinks.system.accessservice.domain.port.in.NotificationUseCase;
import drinks.system.common.dto.ApiResponse;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.security.RequiresPermission;
import drinks.system.common.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/access/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationUseCase notificationUseCase;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> findByUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) String notificationType,
            @RequestParam(defaultValue = "createdAt") String sort) {

        Pageable pageable = PageRequest.of(page, Math.min(size, 100),
                Sort.by(Sort.Direction.DESC, sort));
        PageResponse<NotificationResponse> response = notificationUseCase.findByUser(
                principal.userId(), pageable, isRead, notificationType);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(
            @AuthenticationPrincipal UserPrincipal principal) {

        UnreadCountResponse response = notificationUseCase.getUnreadCount(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        notificationUseCase.markAsRead(id, principal.userId());
        return ResponseEntity.ok(ApiResponse.success(null, "Notificación marcada como leída"));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal UserPrincipal principal) {

        notificationUseCase.markAllAsRead(principal.userId());
        return ResponseEntity.ok(ApiResponse.success(null, "Todas las notificaciones marcadas como leídas"));
    }

    @PostMapping
    @RequiresPermission("CONFIG_PARAMS")
    public ResponseEntity<ApiResponse<NotificationResponse>> create(
            @Valid @RequestBody CreateNotificationRequest request) {

        NotificationResponse response = notificationUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}
