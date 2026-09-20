package drinks.system.salesservice.infrastructure.adapter.out.printer;

import drinks.system.salesservice.domain.model.BranchPrinter;
import drinks.system.salesservice.domain.model.TicketData;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Renders a {@link TicketData} into an ESC/POS byte stream suitable for an
 * 80mm (or 58mm) thermal printer. Uses only the standard ESC/POS command set
 * so it works with Epson TM-series, Xprinter and most compatible printers.
 */
@Component
public class EscPosTicketBuilder {

    // Printers commonly use code page CP437/CP850 for Latin text.
    private static final Charset CHARSET = Charset.forName("CP850");
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("America/El_Salvador");
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(BUSINESS_ZONE);

    // ESC/POS control bytes
    private static final byte ESC = 0x1B;
    private static final byte GS = 0x1D;
    private static final byte LF = 0x0A;

    public byte[] build(TicketData t, BranchPrinter printer) {
        int width = printer.charsPerLine();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        init(out);

        // --- Header (centered, emphasized) ---
        align(out, 1);
        bold(out, true);
        doubleHeight(out, true);
        line(out, safe(t.header()));
        doubleHeight(out, false);
        bold(out, false);

        if (notBlank(t.branchName())) line(out, safe(t.branchName()));
        if (notBlank(t.branchAddress())) line(out, safe(t.branchAddress()));
        if (notBlank(t.branchPhone())) line(out, "Tel: " + safe(t.branchPhone()));

        if (t.reprint()) {
            feed(out, 1);
            bold(out, true);
            line(out, "** REIMPRESION **");
            bold(out, false);
        }

        // --- Sale meta (left aligned) ---
        align(out, 0);
        separator(out, width);
        line(out, "Ticket: " + safe(t.saleNumber()));
        if (t.saleDate() != null) line(out, "Fecha:  " + DATE_FMT.format(t.saleDate()));
        if (notBlank(t.cashierName())) line(out, "Cajero: " + safe(t.cashierName()));
        if (notBlank(t.tableNumber())) line(out, "Mesa:   " + safe(t.tableNumber()));
        if (notBlank(t.customerName())) line(out, "Cliente:" + safe(t.customerName()));
        line(out, "Pago:   " + paymentLabel(t.paymentMethod()));
        separator(out, width);

        // --- Items ---
        // Layout: "qtyx name .... total" — name wraps, amount right-aligned.
        for (TicketData.Line item : t.items()) {
            String qtyName = item.quantity() + "x " + safe(item.productName());
            String amount = money(item.subtotal());
            line(out, twoColumns(qtyName, amount, width));
            // Unit price hint when quantity > 1
            if (item.quantity() > 1) {
                line(out, "   (" + money(item.unitPrice()) + " c/u)");
            }
        }
        separator(out, width);

        // --- Totals (right side) ---
        line(out, twoColumns("Subtotal", money(t.subtotal()), width));
        if (isPositive(t.discountAmount())) {
            line(out, twoColumns("Descuento", "-" + money(t.discountAmount()), width));
        }
        if (isPositive(t.taxAmount())) {
            line(out, twoColumns("Impuesto", money(t.taxAmount()), width));
        }
        bold(out, true);
        doubleHeight(out, true);
        line(out, twoColumns("TOTAL", money(t.totalAmount()), halfWidth(width)));
        doubleHeight(out, false);
        bold(out, false);

        // --- Footer (centered) ---
        feed(out, 1);
        align(out, 1);
        if (notBlank(t.footer())) line(out, safe(t.footer()));

        feed(out, 3);

        if (Boolean.TRUE.equals(printer.openDrawer())) openDrawer(out);
        if (Boolean.TRUE.equals(printer.cutPaper())) cut(out);

        return out.toByteArray();
    }

    /** Minimal test ticket to verify connectivity. */
    public byte[] buildTest(BranchPrinter printer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        init(out);
        align(out, 1);
        bold(out, true);
        doubleHeight(out, true);
        line(out, "PRUEBA DE IMPRESION");
        doubleHeight(out, false);
        bold(out, false);
        line(out, safe(printer.name()));
        line(out, printer.host() + ":" + printer.port());
        line(out, printer.paperWidthMm() + "mm");
        feed(out, 3);
        if (Boolean.TRUE.equals(printer.cutPaper())) cut(out);
        return out.toByteArray();
    }

    // ----- ESC/POS primitives -----

    private void init(ByteArrayOutputStream out) {
        out.write(ESC);
        out.write('@'); // Initialize printer
        // Select character code table CP850 (Multilingual)
        out.write(ESC);
        out.write('t');
        out.write(2);
    }

    private void align(ByteArrayOutputStream out, int mode) { // 0 left, 1 center, 2 right
        out.write(ESC);
        out.write('a');
        out.write(mode);
    }

    private void bold(ByteArrayOutputStream out, boolean on) {
        out.write(ESC);
        out.write('E');
        out.write(on ? 1 : 0);
    }

    private void doubleHeight(ByteArrayOutputStream out, boolean on) {
        // GS ! n — n bits: 0x10 double height, 0x20 double width
        out.write(GS);
        out.write('!');
        out.write(on ? 0x10 : 0x00);
    }

    private void feed(ByteArrayOutputStream out, int lines) {
        for (int i = 0; i < lines; i++) out.write(LF);
    }

    private void line(ByteArrayOutputStream out, String text) {
        writeText(out, text);
        out.write(LF);
    }

    private void writeText(ByteArrayOutputStream out, String text) {
        byte[] bytes = text.getBytes(CHARSET);
        out.write(bytes, 0, bytes.length);
    }

    private void separator(ByteArrayOutputStream out, int width) {
        line(out, "-".repeat(width));
    }

    private void cut(ByteArrayOutputStream out) {
        feed(out, 1);
        out.write(GS);
        out.write('V');
        out.write(66); // Function B: feed and partial cut
        out.write(0);
    }

    private void openDrawer(ByteArrayOutputStream out) {
        // ESC p m t1 t2 — pulse to drawer kick connector pin 2
        out.write(ESC);
        out.write('p');
        out.write(0);
        out.write(25);
        out.write(250);
    }

    // ----- text helpers -----

    /** Places left text and right text on one line padded to width. */
    private String twoColumns(String left, String right, int width) {
        left = left == null ? "" : left;
        right = right == null ? "" : right;
        int space = width - right.length();
        if (space < 1) space = 1;
        if (left.length() > space - 1) {
            left = left.substring(0, Math.max(0, space - 1));
        }
        int pad = width - left.length() - right.length();
        if (pad < 1) pad = 1;
        return left + " ".repeat(pad) + right;
    }

    private int halfWidth(int width) {
        // TOTAL is printed in double width, so it occupies twice the space; use half the columns.
        return Math.max(16, width / 2);
    }

    private String money(BigDecimal value) {
        BigDecimal v = value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
        return v.toPlainString();
    }

    private boolean isPositive(BigDecimal v) {
        return v != null && v.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String paymentLabel(String method) {
        if (method == null) return "";
        return switch (method) {
            case "CASH" -> "Efectivo";
            case "CARD" -> "Tarjeta";
            case "TRANSFER" -> "Transferencia";
            case "QR" -> "Pago QR";
            case "MIXED" -> "Mixto";
            default -> method;
        };
    }

    // package-private for potential unit tests
    List<String> supportedWidths() {
        return List.of("58", "80");
    }
}
