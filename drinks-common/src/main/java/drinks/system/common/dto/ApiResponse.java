package drinks.system.common.dto;

import java.time.Instant;

/**
 * Generic wrapper for successful API responses.
 * Provides a consistent response structure across all microservices.
 *
 * @param <T> the type of the response data payload
 */
public record ApiResponse<T>(
        T data,
        String message,
        Instant timestamp
) {

    /**
     * Creates a successful response with data and no message.
     *
     * @param data the response payload
     * @param <T>  the type of the payload
     * @return an ApiResponse with the data and current timestamp
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null, Instant.now());
    }

    /**
     * Creates a successful response with data and a descriptive message.
     *
     * @param data    the response payload
     * @param message a descriptive message about the operation result
     * @param <T>     the type of the payload
     * @return an ApiResponse with the data, message, and current timestamp
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message, Instant.now());
    }
}
