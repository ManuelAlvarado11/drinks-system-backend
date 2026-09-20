package drinks.system.salesservice.application.dto.request;

/**
 * Optional body for the print endpoint. When {@code printerId} is null the
 * branch default printer is used.
 */
public record PrintTicketRequest(
        Long printerId,
        Boolean reprint
) {}
