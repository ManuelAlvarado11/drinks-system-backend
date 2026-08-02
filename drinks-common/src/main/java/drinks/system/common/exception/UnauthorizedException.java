package drinks.system.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication is required but not provided or invalid.
 * Maps to HTTP 401 Unauthorized.
 */
public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, HttpStatus.UNAUTHORIZED, cause);
    }
}
