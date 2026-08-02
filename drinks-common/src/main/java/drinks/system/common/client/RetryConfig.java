package drinks.system.common.client;

import java.time.Duration;

/**
 * Configuration constants for inter-service HTTP retry policy.
 * Defines max retries and initial backoff for exponential retry on HTTP 5xx errors.
 */
public final class RetryConfig {

    private RetryConfig() {
        // Utility class - prevent instantiation
    }

    /**
     * Maximum number of retry attempts after the initial call fails.
     */
    public static final int MAX_RETRIES = 2;

    /**
     * Initial backoff duration before the first retry.
     * Subsequent retries use exponential backoff: 500ms, 1000ms.
     */
    public static final Duration INITIAL_BACKOFF = Duration.ofMillis(500);
}
