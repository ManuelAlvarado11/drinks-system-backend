package drinks.system.salesservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CancelSaleRequest(
        @NotBlank String reason
) {}
