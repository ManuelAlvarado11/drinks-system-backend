package drinks.system.accessservice.application.dto.response;

import java.time.Instant;

public record SystemParameterResponse(
        Long id,
        String parameterKey,
        String parameterValue,
        String dataType,
        String description,
        String module,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}
