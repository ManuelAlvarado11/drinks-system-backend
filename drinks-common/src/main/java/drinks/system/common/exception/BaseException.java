package drinks.system.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Abstract base exception for all custom application exceptions.
 * Each subclass defines its corresponding HTTP status code.
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private final HttpStatus httpStatus;

    protected BaseException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    protected BaseException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}
