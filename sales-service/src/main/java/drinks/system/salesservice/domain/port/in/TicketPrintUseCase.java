package drinks.system.salesservice.domain.port.in;

/**
 * Prints (or reprints) the receipt for a persisted sale.
 */
public interface TicketPrintUseCase {
    /**
     * Prints the ticket for the given sale.
     *
     * @param saleId    the sale to print
     * @param printerId optional target printer; when null, the branch default printer is used
     * @param reprint   whether to mark the ticket as a reprint (copy)
     */
    void printSale(Long saleId, Long printerId, boolean reprint);
}
