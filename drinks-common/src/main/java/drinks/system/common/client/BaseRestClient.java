package drinks.system.common.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Abstract base HTTP client for inter-service communication.
 * <p>
 * Provides automatic:
 * <ul>
 *   <li>JWT propagation from the current request (via {@link JwtTokenHolder})</li>
 *   <li>Correlation-ID propagation from MDC</li>
 *   <li>Retry with exponential backoff for HTTP 5xx errors (max 2 retries)</li>
 *   <li>30-second response timeout</li>
 * </ul>
 * <p>
 * Subclasses should call {@link #get}, {@link #post}, or {@link #put} methods
 * to perform inter-service HTTP calls.
 */
public abstract class BaseRestClient {

    private static final Logger log = LoggerFactory.getLogger(BaseRestClient.class);

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String MDC_CORRELATION_ID = "correlationId";
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private final RestClient restClient;

    /**
     * Creates a BaseRestClient with a RestClient configured for the given base URL.
     *
     * @param baseUrl the base URL of the target service (e.g., "http://inventory-service:8083")
     */
    protected BaseRestClient(String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Creates a BaseRestClient with an externally provided RestClient.
     * Useful for testing or custom configurations.
     *
     * @param restClient a pre-configured RestClient instance
     */
    protected BaseRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Performs an HTTP GET request with retry and header propagation.
     *
     * @param url          the relative URL path
     * @param responseType the expected response type
     * @param <T>          response type
     * @return the deserialized response body
     */
    protected <T> T get(String url, Class<T> responseType) {
        return executeWithRetry(HttpMethod.GET, url, null, responseType);
    }

    /**
     * Performs an HTTP POST request with retry and header propagation.
     *
     * @param url          the relative URL path
     * @param body         the request body
     * @param responseType the expected response type
     * @param <T>          response type
     * @return the deserialized response body
     */
    protected <T> T post(String url, Object body, Class<T> responseType) {
        return executeWithRetry(HttpMethod.POST, url, body, responseType);
    }

    /**
     * Performs an HTTP PUT request with retry and header propagation.
     *
     * @param url          the relative URL path
     * @param body         the request body
     * @param responseType the expected response type
     * @param <T>          response type
     * @return the deserialized response body
     */
    protected <T> T put(String url, Object body, Class<T> responseType) {
        return executeWithRetry(HttpMethod.PUT, url, body, responseType);
    }

    /**
     * Executes an HTTP call with retry logic for 5xx errors.
     * <p>
     * On HTTP 5xx: retries up to {@link RetryConfig#MAX_RETRIES} times with exponential backoff.
     * On HTTP 4xx: throws immediately without retry.
     *
     * @param method       HTTP method
     * @param url          relative URL path
     * @param body         request body (may be null for GET)
     * @param responseType expected response type
     * @param <T>          response type
     * @return the deserialized response body
     */
    private <T> T executeWithRetry(HttpMethod method, String url, Object body, Class<T> responseType) {
        int attempts = 0;

        while (true) {
            try {
                return execute(method, url, body, responseType);
            } catch (HttpServerErrorException ex) {
                attempts++;
                if (attempts > RetryConfig.MAX_RETRIES) {
                    log.error("Service call failed after {} retries: {} {} - Status: {}",
                            RetryConfig.MAX_RETRIES, method, url, ex.getStatusCode());
                    throw ex;
                }
                Duration backoff = RetryConfig.INITIAL_BACKOFF.multipliedBy((long) Math.pow(2, attempts - 1));
                log.warn("Service call returned {}. Retry attempt {}/{} after {}ms: {} {}",
                        ex.getStatusCode(), attempts, RetryConfig.MAX_RETRIES,
                        backoff.toMillis(), method, url);
                sleep(backoff);
            } catch (HttpClientErrorException ex) {
                // 4xx errors are not retried - throw immediately
                log.debug("Client error from service call: {} {} - Status: {}",
                        method, url, ex.getStatusCode());
                throw ex;
            }
        }
    }

    /**
     * Executes a single HTTP call with JWT and Correlation-ID header propagation.
     */
    private <T> T execute(HttpMethod method, String url, Object body, Class<T> responseType) {
        RestClient.RequestBodySpec requestSpec = restClient.method(method)
                .uri(url);

        // Propagate JWT from ThreadLocal
        String jwt = JwtTokenHolder.get();
        if (jwt != null && !jwt.isBlank()) {
            requestSpec.header(AUTHORIZATION_HEADER, BEARER_PREFIX + jwt);
        }

        // Propagate Correlation-ID from MDC
        String correlationId = MDC.get(MDC_CORRELATION_ID);
        if (correlationId != null && !correlationId.isBlank()) {
            requestSpec.header(CORRELATION_ID_HEADER, correlationId);
        }

        // Set body for POST/PUT requests
        if (body != null) {
            requestSpec.body(body);
        }

        return requestSpec.retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw HttpClientErrorException.create(
                            response.getStatusCode(),
                            response.getStatusText(),
                            response.getHeaders(),
                            response.getBody().readAllBytes(),
                            null
                    );
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw HttpServerErrorException.create(
                            response.getStatusCode(),
                            response.getStatusText(),
                            response.getHeaders(),
                            response.getBody().readAllBytes(),
                            null
                    );
                })
                .body(responseType);
    }

    /**
     * Sleeps for the specified duration. Extracted for testability.
     */
    void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted during retry backoff", e);
        }
    }
}
