package drinks.system.salesservice.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BranchPrinterRequest(
        @NotNull Long branchId,
        @NotBlank String name,
        @NotBlank String host,
        @NotNull @Min(1) @Max(65535) Integer port,
        @NotNull Integer paperWidthMm,
        @Min(1) @Max(5) Integer copies,
        Boolean cutPaper,
        Boolean openDrawer,
        Boolean isDefault,
        Boolean isActive
) {}
