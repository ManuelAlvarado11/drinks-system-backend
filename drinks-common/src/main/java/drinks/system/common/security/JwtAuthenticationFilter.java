package drinks.system.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import drinks.system.common.exception.ErrorResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

/**
 * JWT authentication filter that validates Bearer tokens on every request.
 * <p>
 * Behavior:
 * <ul>
 *   <li>No token present: continues filter chain without authentication (public endpoints)</li>
 *   <li>Invalid/expired token: returns 401 Unauthorized with a JSON ErrorResponse</li>
 *   <li>Valid token: sets SecurityContext with UserPrincipal and adds userId to MDC for logging</li>
 * </ul>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token == null) {
            // No token present - continue without authentication
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtTokenProvider.validateToken(token)) {
            // Invalid or expired token - respond with 401
            writeErrorResponse(response, request);
            return;
        }

        // Token is valid - extract claims and set security context
        Claims claims = jwtTokenProvider.getClaims(token);
        UserPrincipal principal = UserPrincipal.fromClaims(claims);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Add userId to MDC for structured logging
        MDC.put("userId", principal.userId().toString());

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("userId");
        }
    }

    /**
     * Extracts the JWT token from the Authorization header.
     *
     * @param request the HTTP request
     * @return the token string, or null if not present or malformed
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(SecurityConstants.AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            return header.substring(SecurityConstants.TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * Writes a 401 JSON error response directly to the HttpServletResponse.
     */
    private void writeErrorResponse(HttpServletResponse response, HttpServletRequest request) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message("Token inválido o expirado")
                .path(request.getRequestURI())
                .correlationId(MDC.get("correlationId"))
                .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        OBJECT_MAPPER.writeValue(response.getOutputStream(), errorResponse);
    }
}
