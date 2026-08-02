package drinks.system.accessservice.application.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AssignBranchesRequest(
        @NotEmpty List<Long> branchIds
) {
}
