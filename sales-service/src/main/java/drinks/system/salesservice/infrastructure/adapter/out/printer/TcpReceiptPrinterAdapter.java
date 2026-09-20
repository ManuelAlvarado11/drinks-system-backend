package drinks.system.salesservice.infrastructure.adapter.out.printer;

import drinks.system.salesservice.domain.model.BranchPrinter;
import drinks.system.salesservice.domain.model.TicketData;
import drinks.system.salesservice.domain.port.out.ReceiptPrinterPort;
import drinks.system.common.exception.BusinessConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Sends the rendered ESC/POS ticket to a network thermal printer over a raw
 * TCP connection (RAW / JetDirect, typically port 9100). Works for both
 * laptop and tablet clients because the print happens server-side.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TcpReceiptPrinterAdapter implements ReceiptPrinterPort {

    private static final int CONNECT_TIMEOUT_MS = 4000;

    private final EscPosTicketBuilder builder;

    @Override
    public void print(BranchPrinter printer, TicketData ticket) {
        byte[] payload = builder.build(ticket, printer);
        int copies = printer.copies() == null || printer.copies() < 1 ? 1 : printer.copies();
        send(printer, payload, copies);
    }

    /** Sends a connectivity test ticket. */
    public void printTest(BranchPrinter printer) {
        send(printer, builder.buildTest(printer), 1);
    }

    private void send(BranchPrinter printer, byte[] payload, int copies) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(printer.host(), printer.port()), CONNECT_TIMEOUT_MS);
            try (OutputStream os = socket.getOutputStream()) {
                for (int i = 0; i < copies; i++) {
                    os.write(payload);
                }
                os.flush();
            }
            log.info("Ticket enviado a impresora '{}' ({}:{}) x{} copia(s)",
                    printer.name(), printer.host(), printer.port(), copies);
        } catch (IOException e) {
            log.error("No se pudo imprimir en '{}' ({}:{}): {}",
                    printer.name(), printer.host(), printer.port(), e.getMessage());
            throw new BusinessConflictException(
                    "No se pudo conectar con la impresora '" + printer.name() + "' ("
                            + printer.host() + ":" + printer.port() + "). Verifique que esté encendida y en red.");
        }
    }
}
