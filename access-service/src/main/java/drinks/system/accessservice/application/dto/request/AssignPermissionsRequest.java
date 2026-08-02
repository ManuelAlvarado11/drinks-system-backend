package drinks.system.accessservice.application.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AssignPermissionsRequest(
        @NotNull List<Long> permissionIds
) {
}
