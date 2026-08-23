package drinks.system.salesservice.infrastructure.adapter.out.persistence.resolver;

import drinks.system.salesservice.domain.port.out.NameResolverPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Resolves human-readable names from foreign key IDs using cross-schema queries.
 * sales_user has SELECT access on inventory.products and access.users/branches.
 */
@Repository @RequiredArgsConstructor
public class NameResolverAdapter implements NameResolverPort {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<Long, String> findProductNamesByIds(Set<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) return Map.of();
        String placeholders = String.join(",", productIds.stream().map(String::valueOf).toList());
        String sql = "SELECT id, name FROM inventory.products WHERE id IN (" + placeholders + ")";
        Map<Long, String> result = new HashMap<>();
        jdbcTemplate.query(sql, rs -> { result.put(rs.getLong("id"), rs.getString("name")); });
        return result;
    }

    @Override
    public Map<Long, String> findUsernamesByIds(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return Map.of();
        String placeholders = String.join(",", userIds.stream().map(String::valueOf).toList());
        String sql = "SELECT id, full_name FROM access.users WHERE id IN (" + placeholders + ")";
        Map<Long, String> result = new HashMap<>();
        jdbcTemplate.query(sql, rs -> { result.put(rs.getLong("id"), rs.getString("full_name")); });
        return result;
    }
}
