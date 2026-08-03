package drinks.system.accessservice.application.mapper;

import drinks.system.accessservice.application.dto.response.BranchResponse;
import drinks.system.accessservice.application.dto.response.RoleResponse;
import drinks.system.accessservice.application.dto.response.UserDetailResponse;
import drinks.system.accessservice.application.dto.response.UserProfileResponse;
import drinks.system.accessservice.application.dto.response.UserResponse;
import drinks.system.accessservice.domain.model.User;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getPasswordHash(),
                entity.getEmail(),
                entity.getFullName(),
                entity.getBranchId(),
                entity.getIsActive(),
                entity.getLastLogin(),
                entity.getDeletedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity();
        entity.setId(domain.id());
        entity.setUsername(domain.username());
        entity.setPasswordHash(domain.passwordHash());
        entity.setEmail(domain.email());
        entity.setFullName(domain.fullName());
        entity.setBranchId(domain.branchId());
        entity.setIsActive(domain.isActive());
        entity.setLastLogin(domain.lastLogin());
        entity.setDeletedAt(domain.deletedAt());
        entity.setCreatedBy(domain.createdBy());
        entity.setUpdatedBy(domain.updatedBy());
        return entity;
    }

    public UserResponse toResponse(User user) {
        List<String> roleNames = user.roles() != null
                ? user.roles().stream().map(r -> r.name()).toList()
                : Collections.emptyList();

        return new UserResponse(
                user.id(),
                user.username(),
                user.email(),
                user.fullName(),
                user.branchId(),
                user.isActive(),
                user.lastLogin(),
                user.createdAt(),
                roleNames
        );
    }

    public UserDetailResponse toDetailResponse(User user, List<RoleResponse> roles, List<BranchResponse> branches) {
        return new UserDetailResponse(
                user.id(),
                user.username(),
                user.email(),
                user.fullName(),
                user.branchId(),
                user.isActive(),
                user.lastLogin(),
                user.deletedAt(),
                user.createdAt(),
                user.updatedAt(),
                user.createdBy(),
                user.updatedBy(),
                roles,
                branches
        );
    }

    public UserProfileResponse toProfileResponse(User user, List<String> roles, List<String> permissions) {
        return new UserProfileResponse(
                user.id(),
                user.username(),
                user.email(),
                user.fullName(),
                user.branchId(),
                roles,
                permissions
        );
    }
}
