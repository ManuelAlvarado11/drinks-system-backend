package drinks.system.common.logging;

/**
 * Constants for MDC (Mapped Diagnostic Context) keys used across all microservices
 * to provide structured logging context.
 */
public final class MdcConstants {

    private MdcConstants() {
        // Utility class - prevent instantiation
    }

    /**
     * MDC key for the correlation ID that traces a request across services.
     */
    public static final String CORRELATION_ID = "correlationId";

    /**
     * MDC key for the authenticated user ID.
     */
    public static final String USER_ID = "userId";

    /**
     * MDC key for the service name.
     */
    public static final String SERVICE_NAME = "service";

    /**
     * HTTP header name for correlation ID propagation.
     */
    public static final String CORRELATION_HEADER = "X-Correlation-ID";
}
