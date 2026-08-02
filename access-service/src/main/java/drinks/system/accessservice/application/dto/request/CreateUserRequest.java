package drinks.system.accessservice.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(max = 50) String username,
        @NotBlank @Size(min = 8) String password,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(max = 200) String fullName,
        @NotNull Long branchId
) {
}
