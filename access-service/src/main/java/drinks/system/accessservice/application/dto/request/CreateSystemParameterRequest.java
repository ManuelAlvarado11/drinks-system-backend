package drinks.system.accessservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSystemParameterRequest(
        @NotBlank @Size(max = 100) String parameterKey,
        @NotBlank String parameterValue,
        @NotBlank String dataType,
        @Size(max = 300) String description,
        @Size(max = 50) String module
) {
}
