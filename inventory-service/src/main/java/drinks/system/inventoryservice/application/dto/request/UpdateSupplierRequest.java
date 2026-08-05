package drinks.system.inventoryservice.application.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateSupplierRequest(
        @Size(max = 200) String name,
        @Size(max = 150) String contactName,
        @Size(max = 20) String phone,
        @Size(max = 150) String email,
        @Size(max = 300) String address,
        @Size(max = 30) String nit
) {}
