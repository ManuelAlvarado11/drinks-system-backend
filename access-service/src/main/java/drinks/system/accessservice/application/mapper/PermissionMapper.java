package drinks.system.accessservice.application.mapper;

import drinks.system.accessservice.application.dto.response.PermissionResponse;
import drinks.system.accessservice.application.dto.response.PermissionsByModuleResponse;
import drinks.system.accessservice.domain.model.Permission;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PermissionMapper {

    public Permission toDomain(PermissionEntity entity) {
        return new Permission(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getModule(),
                entity.getIsActive()
        );
    }

    public PermissionResponse toResponse(Permission permission) {
        return new PermissionResponse(
                permission.id(),
                permission.code(),
                permission.name(),
                permission.description(),
                permission.module(),
                permission.isActive()
        );
    }

    public List<PermissionsByModuleResponse> toGroupedByModule(List<Permission> permissions) {
        Map<String, List<Permission>> grouped = permissions.stream()
                .collect(Collectors.groupingBy(Permission::module));

        return grouped.entrySet().stream()
                .map(entry -> new PermissionsByModuleResponse(
                        entry.getKey(),
                        entry.getValue().stream().map(this::toResponse).toList()
                ))
                .sorted((a, b) -> a.module().compareTo(b.module()))
                .toList();
    }
}
