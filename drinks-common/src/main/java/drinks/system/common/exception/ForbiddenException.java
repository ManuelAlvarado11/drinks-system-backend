package drinks.system.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the authenticated user lacks the required permission.
 * Maps to HTTP 403 Forbidden.
 */
public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, HttpStatus.FORBIDDEN, cause);
    }

    /**
     * Factory method for permission-based denial.
     */
    public static ForbiddenException forPermission(String permission) {
        return new ForbiddenException(
                String.format("No tiene el permiso requerido: %s", permission));
    }
}
