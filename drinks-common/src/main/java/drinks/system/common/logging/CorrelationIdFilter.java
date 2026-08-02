package drinks.system.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Servlet filter that ensures every request has a correlation ID for end-to-end traceability.
 *
 * <p>Reads the {@code X-Correlation-ID} header from the incoming request. If absent or blank,
 * generates a new UUID. The correlation ID is placed into the MDC for structured logging
 * and set on the response header for client-side tracing.</p>
 *
 * <p>Registered with {@link Ordered#HIGHEST_PRECEDENCE} to run before all other filters,
 * ensuring the correlation ID is available throughout the entire request lifecycle.</p>
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlationId = request.getHeader(MdcConstants.CORRELATION_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(MdcConstants.CORRELATION_ID, correlationId);
        response.setHeader(MdcConstants.CORRELATION_HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MdcConstants.CORRELATION_ID);
        }
    }
}
