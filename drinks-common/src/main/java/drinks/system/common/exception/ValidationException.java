package drinks.system.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when input validation fails.
 * Maps to HTTP 400 Bad Request.
 */
public class ValidationException extends BaseException {

    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, HttpStatus.BAD_REQUEST, cause);
    }
}
