package drinks.system.salesservice.application.dto.response;

import java.time.Instant;

public record CustomerResponse(
        Long id, String firstName, String lastName, String nitCi,
        String phone, String email, Boolean isActive, Instant createdAt, Instant updatedAt
) {}
