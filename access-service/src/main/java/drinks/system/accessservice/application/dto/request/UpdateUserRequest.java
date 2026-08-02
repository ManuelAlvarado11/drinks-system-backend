package drinks.system.accessservice.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Email @Size(max = 150) String email,
        @Size(max = 200) String fullName,
        Long branchId
) {
}
