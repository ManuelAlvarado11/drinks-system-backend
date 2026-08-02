package drinks.system.common.client;

/**
 * Thread-local holder for the raw JWT token string.
 * <p>
 * The JwtAuthenticationFilter should call {@link #set(String)} with the raw token
 * after extracting it from the Authorization header. This allows inter-service clients
 * (like {@link BaseRestClient}) to propagate the original JWT to downstream services
 * without having to reconstruct it from the parsed SecurityContext.
 * <p>
 * Callers MUST invoke {@link #clear()} in a finally block (typically in the filter's
 * doFilter finally) to prevent memory leaks in thread-pooled environments.
 */
public final class JwtTokenHolder {

    private JwtTokenHolder() {
        // Utility class - prevent instantiation
    }

    private static final ThreadLocal<String> TOKEN_HOLDER = new ThreadLocal<>();

    /**
     * Stores the raw JWT token for the current thread/request.
     *
     * @param token the raw JWT string (without "Bearer " prefix)
     */
    public static void set(String token) {
        TOKEN_HOLDER.set(token);
    }

    /**
     * Retrieves the raw JWT token for the current thread/request.
     *
     * @return the raw JWT string, or null if not set
     */
    public static String get() {
        return TOKEN_HOLDER.get();
    }

    /**
     * Removes the JWT token from the current thread to prevent memory leaks.
     * Must be called in a finally block after the request completes.
     */
    public static void clear() {
        TOKEN_HOLDER.remove();
    }
}
