package drinks.system.salesservice.domain.port.out;

import drinks.system.salesservice.domain.model.BranchPrinter;
import drinks.system.salesservice.domain.model.TicketData;

/**
 * Sends a rendered ticket to a physical ESC/POS printer.
 */
public interface ReceiptPrinterPort {
    /**
     * Renders the ticket to ESC/POS bytes and sends it to the printer over TCP.
     *
     * @throws drinks.system.common.exception.BusinessConflictException if the printer is unreachable.
     */
    void print(BranchPrinter printer, TicketData ticket);
}
