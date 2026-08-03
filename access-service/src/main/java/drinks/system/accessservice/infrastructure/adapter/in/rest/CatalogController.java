package drinks.system.accessservice.infrastructure.adapter.in.rest;

import drinks.system.accessservice.application.dto.request.CreateCatalogRequest;
import drinks.system.accessservice.application.dto.request.UpdateCatalogRequest;
import drinks.system.accessservice.application.dto.response.CatalogResponse;
import drinks.system.accessservice.domain.port.in.CatalogUseCase;
import drinks.system.common.dto.ApiResponse;
import drinks.system.common.security.RequiresPermission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/access/v1/catalogs")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogUseCase catalogUseCase;

    @PostMapping
    @RequiresPermission("CONFIG_CATALOGS")
    public ResponseEntity<ApiResponse<CatalogResponse>> create(
            @Valid @RequestBody CreateCatalogRequest request) {

        CatalogResponse response = catalogUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    @RequiresPermission("CONFIG_CATALOGS")
    public ResponseEntity<ApiResponse<List<CatalogResponse>>> findByType(
            @RequestParam("catalog_type") String catalogType) {

        List<CatalogResponse> response = catalogUseCase.findByType(catalogType);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/types")
    @RequiresPermission("CONFIG_CATALOGS")
    public ResponseEntity<ApiResponse<List<String>>> findDistinctTypes() {
        List<String> response = catalogUseCase.findDistinctTypes();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @RequiresPermission("CONFIG_CATALOGS")
    public ResponseEntity<ApiResponse<CatalogResponse>> findById(@PathVariable Long id) {
        CatalogResponse response = catalogUseCase.findById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @RequiresPermission("CONFIG_CATALOGS")
    public ResponseEntity<ApiResponse<CatalogResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCatalogRequest request) {

        CatalogResponse response = catalogUseCase.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("CONFIG_CATALOGS")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        catalogUseCase.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Catálogo desactivado exitosamente"));
    }
}
