package drinks.system.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Provides JWT generation and validation using HMAC-SHA256.
 * Configurable via Spring properties:
 * - security.jwt.secret: the signing secret (min 32 chars for HMAC-SHA256)
 * - security.jwt.expiration-minutes: token TTL in minutes (default 15)
 */
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationMillis;

    public JwtTokenProvider(
            @Value("${" + SecurityConstants.SECRET_KEY_PROPERTY + "}") String secret,
            @Value("${" + SecurityConstants.EXPIRATION_PROPERTY + ":15}") long expirationMinutes) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMinutes * 60 * 1000;
    }

    /**
     * Generates a signed JWT with the provided user information.
     *
     * @param userId      user identifier (stored as subject)
     * @param username    username claim
     * @param branchId    branch identifier claim
     * @param permissions list of permission strings
     * @return signed JWT string
     */
    public String generateToken(String userId, String username, Long branchId, List<String> permissions) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(userId)
                .claim(SecurityConstants.CLAIM_USERNAME, username)
                .claim(SecurityConstants.CLAIM_BRANCH_ID, branchId)
                .claim(SecurityConstants.CLAIM_PERMISSIONS, permissions)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Validates the token signature and expiration.
     *
     * @param token JWT string to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extracts all claims from a valid token.
     * Callers should validate the token first or handle exceptions.
     *
     * @param token JWT string
     * @return parsed claims
     * @throws JwtException if the token is invalid or expired
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
