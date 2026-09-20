package drinks.system.salesservice.domain.port.out;

import java.util.Optional;

/**
 * Cross-schema lookups needed to build a ticket: branch data (access.branches)
 * and system parameters (access.system_parameters) such as TICKET_HEADER/FOOTER.
 */
public interface TicketInfoPort {

    record BranchInfo(String name, String address, String phone) {}

    Optional<BranchInfo> findBranchInfo(Long branchId);

    /** Returns the value of a system parameter, or the given default when missing. */
    String getParameter(String key, String defaultValue);
}
