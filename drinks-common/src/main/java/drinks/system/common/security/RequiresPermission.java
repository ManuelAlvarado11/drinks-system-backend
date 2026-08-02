package drinks.system.common.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom authorization annotation for controller methods.
 * Indicates that the annotated method requires the authenticated user to have
 * the specified permission. If the user lacks the permission, a 403 Forbidden
 * response is returned.
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code @RequiresPermission("USERS_CREATE")}
 * {@code @PostMapping}
 * public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) { ... }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermission {

    /**
     * The permission code required to access the annotated method.
     * Example: "USERS_CREATE", "SALES_READ", "INVENTORY_WRITE"
     */
    String value();
}
