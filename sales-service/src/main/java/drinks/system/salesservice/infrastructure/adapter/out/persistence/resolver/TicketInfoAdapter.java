package drinks.system.salesservice.infrastructure.adapter.out.persistence.resolver;

import drinks.system.salesservice.domain.port.out.TicketInfoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Resolves ticket header data from the access schema (sales_user has SELECT there,
 * see V10 grants): branch info from access.branches and configurable header/footer
 * texts from access.system_parameters.
 */
@Repository
@RequiredArgsConstructor
public class TicketInfoAdapter implements TicketInfoPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<BranchInfo> findBranchInfo(Long branchId) {
        if (branchId == null) return Optional.empty();
        List<BranchInfo> rows = jdbcTemplate.query(
                "SELECT name, address, phone FROM access.branches WHERE id = ?",
                (rs, i) -> new BranchInfo(rs.getString("name"), rs.getString("address"), rs.getString("phone")),
                branchId);
        return rows.stream().findFirst();
    }

    @Override
    public String getParameter(String key, String defaultValue) {
        try {
            List<String> values = jdbcTemplate.query(
                    "SELECT parameter_value FROM access.system_parameters WHERE parameter_key = ?",
                    (rs, i) -> rs.getString("parameter_value"),
                    key);
            return values.stream().findFirst().filter(v -> v != null && !v.isBlank()).orElse(defaultValue);
        } catch (RuntimeException ex) {
            return defaultValue;
        }
    }
}
