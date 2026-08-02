package drinks.system.common.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Custom UserDetails implementation that carries authenticated user information
 * extracted from JWT claims. Used as the principal in the SecurityContext.
 */
public record UserPrincipal(
        Long userId,
        String username,
        Long branchId,
        List<String> permissions
) implements UserDetails {

    /**
     * Creates a UserPrincipal from JWT claims.
     *
     * @param claims the parsed JWT claims
     * @return a new UserPrincipal instance
     */
    @SuppressWarnings("unchecked")
    public static UserPrincipal fromClaims(Claims claims) {
        Long userId = Long.valueOf(claims.getSubject());
        String username = claims.get(SecurityConstants.CLAIM_USERNAME, String.class);

        // branchId may come as Integer or Long from JSON deserialization
        Number branchIdNumber = claims.get(SecurityConstants.CLAIM_BRANCH_ID, Number.class);
        Long branchId = branchIdNumber != null ? branchIdNumber.longValue() : null;

        List<String> permissions = claims.get(SecurityConstants.CLAIM_PERMISSIONS, List.class);

        if (permissions == null) {
            permissions = Collections.emptyList();
        }

        return new UserPrincipal(userId, username, branchId, Collections.unmodifiableList(permissions));
    }

    /**
     * Checks whether the user has a specific permission.
     *
     * @param permission the permission string to check
     * @return true if the user has the permission
     */
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        // Password is not available from JWT claims
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
