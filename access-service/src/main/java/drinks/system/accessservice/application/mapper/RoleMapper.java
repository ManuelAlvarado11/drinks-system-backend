package drinks.system.accessservice.application.mapper;

import drinks.system.accessservice.application.dto.response.PermissionResponse;
import drinks.system.accessservice.application.dto.response.RoleDetailResponse;
import drinks.system.accessservice.application.dto.response.RoleResponse;
import drinks.system.accessservice.domain.model.Role;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.RoleEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RoleMapper {

    public Role toDomain(RoleEntity entity) {
        return new Role(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                Collections.emptyList()
        );
    }

    public RoleEntity toEntity(Role domain) {
        RoleEntity entity = new RoleEntity();
        entity.setId(domain.id());
        entity.setCode(domain.code());
        entity.setName(domain.name());
        entity.setDescription(domain.description());
        entity.setIsActive(domain.isActive());
        return entity;
    }

    public RoleResponse toResponse(Role role, int permissionCount, int userCount) {
        return new RoleResponse(
                role.id(),
                role.code(),
                role.name(),
                role.description(),
                role.isActive(),
                role.createdAt(),
                permissionCount,
                userCount
        );
    }

    public RoleDetailResponse toDetailResponse(Role role, List<PermissionResponse> permissions) {
        return new RoleDetailResponse(
                role.id(),
                role.code(),
                role.name(),
                role.description(),
                role.isActive(),
                role.createdAt(),
                role.updatedAt(),
                permissions
        );
    }
}
