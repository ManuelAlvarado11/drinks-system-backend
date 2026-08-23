package drinks.system.accessservice.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record SwitchBranchRequest(
        @NotNull Long branchId
) {}
