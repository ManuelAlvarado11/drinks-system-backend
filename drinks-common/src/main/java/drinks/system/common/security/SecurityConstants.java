package drinks.system.common.security;

/**
 * Security constants used across all microservices for JWT handling.
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // Utility class - prevent instantiation
    }

    /**
     * HTTP header name for authorization.
     */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Bearer token prefix (includes trailing space).
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Property name for the JWT secret key in application configuration.
     */
    public static final String SECRET_KEY_PROPERTY = "security.jwt.secret";

    /**
     * Property name for the JWT expiration in minutes.
     */
    public static final String EXPIRATION_PROPERTY = "security.jwt.expiration-minutes";

    /**
     * JWT claim key for username.
     */
    public static final String CLAIM_USERNAME = "username";

    /**
     * JWT claim key for branch ID.
     */
    public static final String CLAIM_BRANCH_ID = "branchId";

    /**
     * JWT claim key for permissions list.
     */
    public static final String CLAIM_PERMISSIONS = "permissions";
}
