package drinks.system.salesservice.application.dto.response;

public record BranchPrinterResponse(
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
        Boolean isActive
) {}
