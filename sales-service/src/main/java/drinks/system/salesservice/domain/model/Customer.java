package drinks.system.salesservice.domain.model;

import java.time.Instant;

public record Customer(
        Long id,
        String firstName,
        String lastName,
        String nitCi,
        String phone,
        String email,
        Boolean isActive,
        Instant deletedAt,
        Instant createdAt,
        Instant updatedAt,
        Long createdBy,
        Long updatedBy
) {
}
