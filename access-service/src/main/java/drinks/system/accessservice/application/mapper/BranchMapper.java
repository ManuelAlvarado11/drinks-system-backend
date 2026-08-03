package drinks.system.accessservice.application.mapper;

import drinks.system.accessservice.application.dto.response.BranchResponse;
import drinks.system.accessservice.domain.model.Branch;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.BranchEntity;
import org.springframework.stereotype.Component;

@Component
public class BranchMapper {

    public Branch toDomain(BranchEntity entity) {
        return new Branch(
                entity.getId(),
                entity.getName(),
                entity.getAddress(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getIsActive(),
                entity.getDeletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }

    public BranchEntity toEntity(Branch domain) {
        BranchEntity entity = new BranchEntity();
        entity.setId(domain.id());
        entity.setName(domain.name());
        entity.setAddress(domain.address());
        entity.setPhone(domain.phone());
        entity.setEmail(domain.email());
        entity.setIsActive(domain.isActive());
        entity.setDeletedAt(domain.deletedAt());
        entity.setCreatedBy(domain.createdBy());
        entity.setUpdatedBy(domain.updatedBy());
        return entity;
    }

    public BranchResponse toResponse(Branch branch) {
        return new BranchResponse(
                branch.id(),
                branch.name(),
                branch.address(),
                branch.phone(),
                branch.email(),
                branch.isActive(),
                branch.createdAt(),
                branch.updatedAt()
        );
    }
}
