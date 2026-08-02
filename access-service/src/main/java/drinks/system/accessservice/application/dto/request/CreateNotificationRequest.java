package drinks.system.accessservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateNotificationRequest(
        Long branchId,
        @NotNull Long userId,
        @NotBlank @Size(max = 50) String notificationType,
        @NotBlank @Size(max = 200) String title,
        @NotBlank String message
) {
}
