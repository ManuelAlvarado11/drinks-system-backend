package drinks.system.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource cannot be found.
 * Maps to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(String.format("%s con ID %s no encontrado", resourceName, resourceId), HttpStatus.NOT_FOUND);
    }
}
