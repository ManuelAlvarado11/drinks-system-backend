package drinks.system.salesservice.application.mapper;

import drinks.system.salesservice.application.dto.response.CustomerResponse;
import drinks.system.salesservice.domain.model.Customer;
import drinks.system.salesservice.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toDomain(CustomerEntity entity) {
        return new Customer(entity.getId(), entity.getFirstName(), entity.getLastName(),
                entity.getNitCi(), entity.getPhone(), entity.getEmail(), entity.getIsActive(),
                entity.getDeletedAt(), entity.getCreatedAt(), entity.getUpdatedAt(),
                entity.getCreatedBy(), entity.getUpdatedBy());
    }

    public CustomerEntity toEntity(Customer domain) {
        CustomerEntity e = new CustomerEntity();
        e.setId(domain.id());
        e.setFirstName(domain.firstName());
        e.setLastName(domain.lastName());
        e.setNitCi(domain.nitCi());
        e.setPhone(domain.phone());
        e.setEmail(domain.email());
        e.setIsActive(domain.isActive());
        e.setDeletedAt(domain.deletedAt());
        e.setCreatedBy(domain.createdBy());
        e.setUpdatedBy(domain.updatedBy());
        return e;
    }

    public CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(c.id(), c.firstName(), c.lastName(), c.nitCi(),
                c.phone(), c.email(), c.isActive(), c.createdAt(), c.updatedAt());
    }
}
