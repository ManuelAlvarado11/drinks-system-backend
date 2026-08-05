package drinks.system.inventoryservice.application.dto.response;

import java.time.Instant;

public record SupplierResponse(
        Long id, String name, String contactName, String phone,
        String email, String address, String nit, Boolean isActive,
        Instant createdAt, Instant updatedAt
) {}
