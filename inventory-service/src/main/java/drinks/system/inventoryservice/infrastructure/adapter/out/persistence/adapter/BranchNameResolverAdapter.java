package drinks.system.inventoryservice.infrastructure.adapter.out.persistence.adapter;

import drinks.system.inventoryservice.domain.port.out.BranchNameResolverPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Repository @RequiredArgsConstructor
public class BranchNameResolverAdapter implements BranchNameResolverPort {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<Long, String> findNamesByIds(Set<Long> branchIds) {
        if (branchIds == null || branchIds.isEmpty()) return Map.of();
        String placeholders = String.join(",", branchIds.stream().map(String::valueOf).toList());
        String sql = "SELECT id, name FROM access.branches WHERE id IN (" + placeholders + ")";
        Map<Long, String> result = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            result.put(rs.getLong("id"), rs.getString("name"));
        });
        return result;
    }
}
