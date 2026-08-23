package drinks.system.salesservice.domain.port.out;

import java.util.Map;
import java.util.Set;

/**
 * Port for resolving human-readable names from foreign key IDs (cross-schema lookups).
 */
public interface NameResolverPort {
    Map<Long, String> findProductNamesByIds(Set<Long> productIds);
    Map<Long, String> findUsernamesByIds(Set<Long> userIds);
}
