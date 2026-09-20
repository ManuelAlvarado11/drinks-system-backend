package drinks.system.salesservice.domain.model;

import java.time.Instant;

/**
 * ESC/POS network printer configured for a branch.
 * Prints are sent as raw bytes over TCP to {@code host:port} (RAW / port 9100).
 */
public record BranchPrinter(
        Long id,
        Long branchId,
        String name,
        String host,
        Integer port,
        Integer paperWidthMm,
        Integer copies,
        Boolean cutPaper,
        Boolean openDrawer,
        Boolean isDefault,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt,
        Long createdBy,
        Long updatedBy
) {
    /** Characters per line for the configured paper width (Font A). */
    public int charsPerLine() {
        return paperWidthMm != null && paperWidthMm == 58 ? 32 : 48;
    }
}
