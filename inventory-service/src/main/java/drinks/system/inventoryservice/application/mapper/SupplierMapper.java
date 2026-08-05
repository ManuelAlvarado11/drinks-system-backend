package drinks.system.inventoryservice.application.mapper;

import drinks.system.inventoryservice.application.dto.response.SupplierResponse;
import drinks.system.inventoryservice.domain.model.Supplier;
import drinks.system.inventoryservice.infrastructure.adapter.out.persistence.entity.SupplierEntity;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {
    public Supplier toDomain(SupplierEntity e) {
        return new Supplier(e.getId(), e.getName(), e.getContactName(), e.getPhone(),
                e.getEmail(), e.getAddress(), e.getNit(), e.getIsActive(),
                e.getDeletedAt(), e.getCreatedAt(), e.getUpdatedAt(), e.getCreatedBy(), e.getUpdatedBy());
    }
    public SupplierEntity toEntity(Supplier d) {
        SupplierEntity e = new SupplierEntity();
        e.setId(d.id()); e.setName(d.name()); e.setContactName(d.contactName());
        e.setPhone(d.phone()); e.setEmail(d.email()); e.setAddress(d.address());
        e.setNit(d.nit()); e.setIsActive(d.isActive()); e.setDeletedAt(d.deletedAt());
        e.setCreatedBy(d.createdBy()); e.setUpdatedBy(d.updatedBy());
        return e;
    }
    public SupplierResponse toResponse(Supplier s) {
        return new SupplierResponse(s.id(), s.name(), s.contactName(), s.phone(),
                s.email(), s.address(), s.nit(), s.isActive(), s.createdAt(), s.updatedAt());
    }
}
