package drinks.system.salesservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(
        @NotBlank @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Size(max = 30) String nitCi,
        @Size(max = 20) String phone,
        @Size(max = 150) String email
) {}
