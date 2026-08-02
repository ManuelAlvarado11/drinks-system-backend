package drinks.system.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a business rule conflict occurs.
 * Maps to HTTP 409 Conflict.
 */
public class BusinessConflictException extends BaseException {

    public BusinessConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public BusinessConflictException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT, cause);
    }
}
