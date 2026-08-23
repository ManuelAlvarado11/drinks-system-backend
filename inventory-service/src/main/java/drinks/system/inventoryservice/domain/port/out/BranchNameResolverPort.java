package drinks.system.inventoryservice.domain.port.out;

import java.util.Map;
import java.util.Set;

public interface BranchNameResolverPort {
    Map<Long, String> findNamesByIds(Set<Long> branchIds);
}
