package drinks.system.inventoryservice.domain.model;

import java.time.Instant;

public record Supplier(
        Long id, String name, String contactName, String phone,
        String email, String address, String nit, Boolean isActive,
        Instant deletedAt, Instant createdAt, Instant updatedAt,
        Long createdBy, Long updatedBy
) {}
