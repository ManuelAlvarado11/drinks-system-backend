package drinks.system.accessservice.application.mapper;

import drinks.system.accessservice.application.dto.response.MenuOptionResponse;
import drinks.system.accessservice.application.dto.response.MenuTreeResponse;
import drinks.system.accessservice.domain.model.SystemMenuOption;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.SystemMenuOptionEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MenuOptionMapper {

    public SystemMenuOption toDomain(SystemMenuOptionEntity entity) {
        return new SystemMenuOption(
                entity.getId(),
                entity.getName(),
                entity.getRoute(),
                entity.getIcon(),
                entity.getParentId(),
                entity.getPermissionId(),
                entity.getSortOrder(),
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public SystemMenuOptionEntity toEntity(SystemMenuOption domain) {
        SystemMenuOptionEntity entity = new SystemMenuOptionEntity();
        entity.setId(domain.id());
        entity.setName(domain.name());
        entity.setRoute(domain.route());
        entity.setIcon(domain.icon());
        entity.setParentId(domain.parentId());
        entity.setPermissionId(domain.permissionId());
        entity.setSortOrder(domain.sortOrder());
        entity.setIsActive(domain.isActive());
        return entity;
    }

    public MenuOptionResponse toResponse(SystemMenuOption option) {
        return new MenuOptionResponse(
                option.id(),
                option.name(),
                option.route(),
                option.icon(),
                option.parentId(),
                option.permissionId(),
                option.sortOrder(),
                option.isActive(),
                option.createdAt(),
                option.updatedAt()
        );
    }

    public List<MenuTreeResponse> buildTree(List<SystemMenuOption> options) {
        Map<Long, List<SystemMenuOption>> childrenByParent = options.stream()
                .filter(o -> o.parentId() != null)
                .collect(Collectors.groupingBy(SystemMenuOption::parentId));

        List<SystemMenuOption> roots = options.stream()
                .filter(o -> o.parentId() == null)
                .sorted(Comparator.comparingInt(SystemMenuOption::sortOrder))
                .toList();

        return roots.stream()
                .map(root -> buildNode(root, childrenByParent))
                .toList();
    }

    private MenuTreeResponse buildNode(SystemMenuOption option, Map<Long, List<SystemMenuOption>> childrenByParent) {
        List<SystemMenuOption> children = childrenByParent.getOrDefault(option.id(), new ArrayList<>());
        List<MenuTreeResponse> childNodes = children.stream()
                .sorted(Comparator.comparingInt(SystemMenuOption::sortOrder))
                .map(child -> buildNode(child, childrenByParent))
                .toList();

        return new MenuTreeResponse(
                option.id(),
                option.name(),
                option.route(),
                option.icon(),
                option.sortOrder(),
                childNodes
        );
    }
}
