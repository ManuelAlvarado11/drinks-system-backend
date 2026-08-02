package drinks.system.accessservice.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateBranchRequest(
        @Size(max = 150) String name,
        @Size(max = 300) String address,
        @Size(max = 20) String phone,
        @Email @Size(max = 150) String email
) {
}
