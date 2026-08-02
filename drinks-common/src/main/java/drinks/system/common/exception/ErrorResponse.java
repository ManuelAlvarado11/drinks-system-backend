package drinks.system.common.exception;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error response DTO returned by all microservices.
 * Follows the format: timestamp, status, error, message, path, correlationId, fieldErrors.
 */
@Builder
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String correlationId,
        Map<String, String> fieldErrors
) {
}
