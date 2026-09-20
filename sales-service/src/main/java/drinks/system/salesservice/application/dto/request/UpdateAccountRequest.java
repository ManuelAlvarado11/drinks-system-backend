package drinks.system.salesservice.application.dto.request;

/**
 * Payload for PATCH /api/sales/v1/accounts/{id}.
 * All fields are optional — only non-null values are applied.
 */
public record UpdateAccountRequest(
        String customerName,
        String customerLastName,
        String tableNumber,
        String notes
) {}
