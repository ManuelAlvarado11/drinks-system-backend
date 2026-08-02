package drinks.system.accessservice.application.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateSystemParameterRequest(
        String parameterValue,
        String dataType,
        @Size(max = 300) String description,
        @Size(max = 50) String module
) {
}
