package drinks.system.salesservice.infrastructure.adapter.out.client;

import drinks.system.salesservice.domain.model.StockDeductionItem;
import drinks.system.salesservice.domain.port.out.InventoryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InventoryClientImpl implements InventoryClient {

    private final RestClient restClient;

    public InventoryClientImpl(@Value("${services.inventory.url}") String inventoryUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(inventoryUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public void deductStock(List<StockDeductionItem> items, Long branchId) {
        callStockEndpoint("/api/inventory/v1/stock/deduct", items, branchId);
    }

    @Override
    public void addStock(List<StockDeductionItem> items, Long branchId) {
        callStockEndpoint("/api/inventory/v1/stock/add", items, branchId);
    }

    private void callStockEndpoint(String path, List<StockDeductionItem> items, Long branchId) {
        try {
            String token = extractJwtToken();
            var body = Map.of("branchId", branchId, "items", items);

            restClient.post()
                    .uri(path)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            log.debug("Stock operation successful: {} items at branch {}", items.size(), branchId);
        } catch (Exception e) {
            log.warn("Failed to call inventory service at {}: {}. Sale will proceed.", path, e.getMessage());
        }
    }

    private String extractJwtToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() instanceof String token) {
            return token;
        }
        return "";
    }
}
