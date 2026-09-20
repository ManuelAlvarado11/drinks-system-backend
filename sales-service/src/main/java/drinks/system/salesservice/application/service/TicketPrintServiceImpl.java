package drinks.system.salesservice.application.service;

import drinks.system.salesservice.domain.model.Account;
import drinks.system.salesservice.domain.model.BranchPrinter;
import drinks.system.salesservice.domain.model.Sale;
import drinks.system.salesservice.domain.model.SaleDetail;
import drinks.system.salesservice.domain.model.TicketData;
import drinks.system.salesservice.domain.port.in.TicketPrintUseCase;
import drinks.system.salesservice.domain.port.out.*;
import drinks.system.common.exception.BusinessConflictException;
import drinks.system.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketPrintServiceImpl implements TicketPrintUseCase {

    private static final String DEFAULT_HEADER = "BAR DRINKS SYSTEM";
    private static final String DEFAULT_FOOTER = "Gracias por su preferencia";

    private final SaleRepositoryPort saleRepository;
    private final SaleDetailRepositoryPort saleDetailRepository;
    private final AccountRepositoryPort accountRepository;
    private final BranchPrinterRepositoryPort printerRepository;
    private final NameResolverPort nameResolver;
    private final TicketInfoPort ticketInfo;
    private final ReceiptPrinterPort receiptPrinter;

    @Override
    @Transactional(readOnly = true)
    public void printSale(Long saleId, Long printerId, boolean reprint) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", saleId));

        BranchPrinter printer = resolvePrinter(sale.branchId(), printerId);

        TicketData ticket = buildTicket(sale, reprint);
        receiptPrinter.print(printer, ticket);
    }

    private BranchPrinter resolvePrinter(Long branchId, Long printerId) {
        if (printerId != null) {
            return printerRepository.findById(printerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Impresora", printerId));
        }
        return printerRepository.findDefaultByBranchId(branchId)
                .orElseThrow(() -> new BusinessConflictException(
                        "No hay una impresora predeterminada configurada para esta sucursal."));
    }

    private TicketData buildTicket(Sale sale, boolean reprint) {
        List<SaleDetail> details = saleDetailRepository.findBySaleId(sale.id());

        Set<Long> productIds = details.stream().map(SaleDetail::productId).collect(Collectors.toSet());
        Map<Long, String> productNames = nameResolver.findProductNamesByIds(productIds);

        List<TicketData.Line> lines = details.stream()
                .map(d -> new TicketData.Line(
                        productNames.getOrDefault(d.productId(), "Producto #" + d.productId()),
                        d.quantity(), d.unitPrice(), d.subtotal()))
                .toList();

        String cashierName = resolveUserName(sale.createdBy());

        String tableNumber = null;
        String customerName = null;
        if (sale.accountId() != null) {
            Account account = accountRepository.findById(sale.accountId()).orElse(null);
            if (account != null) {
                tableNumber = account.tableNumber();
                customerName = joinName(account.customerName(), account.customerLastName());
            }
        }

        TicketInfoPort.BranchInfo branch = ticketInfo.findBranchInfo(sale.branchId())
                .orElse(new TicketInfoPort.BranchInfo(null, null, null));
        String header = ticketInfo.getParameter("TICKET_HEADER", DEFAULT_HEADER);
        String footer = ticketInfo.getParameter("TICKET_FOOTER", DEFAULT_FOOTER);

        return new TicketData(
                header, footer,
                branch.name(), branch.address(), branch.phone(),
                sale.saleNumber(), sale.saleDate(), sale.paymentMethod(),
                cashierName, customerName, tableNumber,
                lines,
                sale.subtotal(), sale.discountAmount(), sale.taxAmount(), sale.totalAmount(),
                reprint);
    }

    private String resolveUserName(Long userId) {
        if (userId == null) return null;
        return nameResolver.findUsernamesByIds(Set.of(userId)).get(userId);
    }

    private String joinName(String first, String last) {
        String f = first == null ? "" : first.trim();
        String l = last == null ? "" : last.trim();
        String joined = (f + " " + l).trim();
        return joined.isEmpty() ? null : joined;
    }
}
