package drinks.system.accessservice.application.service;

import drinks.system.accessservice.application.dto.request.AdminChangePasswordRequest;
import drinks.system.accessservice.application.dto.request.AssignBranchesRequest;
import drinks.system.accessservice.application.dto.request.AssignRolesRequest;
import drinks.system.accessservice.application.dto.request.ChangeOwnPasswordRequest;
import drinks.system.accessservice.application.dto.request.CreateUserRequest;
import drinks.system.accessservice.application.dto.request.UpdateUserRequest;
import drinks.system.accessservice.application.dto.response.BranchResponse;
import drinks.system.accessservice.application.dto.response.RoleResponse;
import drinks.system.accessservice.application.dto.response.UserDetailResponse;
import drinks.system.accessservice.application.dto.response.UserProfileResponse;
import drinks.system.accessservice.application.dto.response.UserResponse;
import drinks.system.accessservice.application.mapper.BranchMapper;
import drinks.system.accessservice.application.mapper.RoleMapper;
import drinks.system.accessservice.application.mapper.UserMapper;
import drinks.system.accessservice.domain.model.Branch;
import drinks.system.accessservice.domain.model.Permission;
import drinks.system.accessservice.domain.model.Role;
import drinks.system.accessservice.domain.model.User;
import drinks.system.accessservice.domain.port.in.UserUseCase;
import drinks.system.accessservice.domain.port.out.BranchRepositoryPort;
import drinks.system.accessservice.domain.port.out.PermissionRepositoryPort;
import drinks.system.accessservice.domain.port.out.RefreshTokenRepositoryPort;
import drinks.system.accessservice.domain.port.out.RoleRepositoryPort;
import drinks.system.accessservice.domain.port.out.UserRepositoryPort;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.BranchEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.RoleEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.UserBranchEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.UserEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.entity.UserRoleEntity;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.BranchJpaRepository;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.RoleJpaRepository;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.UserBranchJpaRepository;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import drinks.system.accessservice.infrastructure.adapter.out.persistence.repository.UserRoleJpaRepository;
import drinks.system.common.audit.AuditEvent;
import drinks.system.common.dto.PageResponse;
import drinks.system.common.exception.BusinessConflictException;
import drinks.system.common.exception.ResourceNotFoundException;
import drinks.system.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final BranchRepositoryPort branchRepository;
    private final PermissionRepositoryPort permissionRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final BranchJpaRepository branchJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;
    private final UserBranchJpaRepository userBranchJpaRepository;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final BranchMapper branchMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public UserDetailResponse create(CreateUserRequest request, Long currentUserId) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessConflictException("El nombre de usuario ya existe: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessConflictException("El email ya está registrado: " + request.email());
        }

        User user = new User(
                null,
                request.username(),
                passwordEncoder.encode(request.password()),
                request.email(),
                request.fullName(),
                request.branchId(),
                true,
                null, null, null, null,
                currentUserId, currentUserId,
                Collections.emptyList(),
                Collections.emptyList()
        );

        User saved = userRepository.save(user);

        eventPublisher.publishEvent(new AuditEvent(
                currentUserId, null, "CREATE", "ACCESS",
                "User", saved.id(), null, null, null,
                "Usuario creado: " + saved.username()
        ));

        return buildDetailResponse(saved.id());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> findAll(Pageable pageable, Boolean isActive, Long branchId, String search) {
        Page<User> page = userRepository.findAll(pageable, isActive, branchId, search);
        List<UserResponse> content = page.getContent().stream()
                .map(user -> {
                    List<Role> roles = roleRepository.findByUserId(user.id());
                    List<String> roleNames = roles.stream().map(Role::name).toList();
                    return new UserResponse(
                            user.id(), user.username(), user.email(), user.fullName(),
                            user.branchId(), user.isActive(), user.lastLogin(), user.createdAt(),
                            roleNames
                    );
                })
                .toList();
        return PageResponse.of(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse findById(Long id) {
        return buildDetailResponse(id);
    }

    @Override
    @Transactional
    public UserDetailResponse update(Long id, UpdateUserRequest request, Long currentUserId) {
        UserEntity entity = userJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        if (request.email() != null && !request.email().equals(entity.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new BusinessConflictException("El email ya está registrado: " + request.email());
            }
            entity.setEmail(request.email());
        }
        if (request.fullName() != null) {
            entity.setFullName(request.fullName());
        }
        if (request.branchId() != null) {
            entity.setBranchId(request.branchId());
        }
        entity.setUpdatedBy(currentUserId);
        userJpaRepository.save(entity);

        eventPublisher.publishEvent(new AuditEvent(
                currentUserId, null, "UPDATE", "ACCESS",
                "User", id, null, null, null,
                "Usuario actualizado: " + entity.getUsername()
        ));

        return buildDetailResponse(id);
    }

    @Override
    @Transactional
    public void delete(Long id, Long currentUserId) {
        UserEntity entity = userJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        entity.setIsActive(false);
        entity.setDeletedAt(Instant.now());
        entity.setUpdatedBy(currentUserId);
        userJpaRepository.save(entity);

        eventPublisher.publishEvent(new AuditEvent(
                currentUserId, null, "DELETE", "ACCESS",
                "User", id, null, null, null,
                "Usuario desactivado: " + entity.getUsername()
        ));
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, AssignRolesRequest request) {
        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", userId));

        for (Long roleId : request.roleIds()) {
            RoleEntity role = roleJpaRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol", roleId));
            UserRoleEntity ur = new UserRoleEntity();
            ur.setUser(user);
            ur.setRole(role);
            userRoleJpaRepository.save(ur);
        }
    }

    @Override
    @Transactional
    public void removeRole(Long userId, Long roleId) {
        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", userId));
        RoleEntity role = roleJpaRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", roleId));
        userRoleJpaRepository.deleteByUserAndRole(user, role);
    }

    @Override
    @Transactional
    public void assignBranches(Long userId, AssignBranchesRequest request) {
        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", userId));

        for (Long branchId : request.branchIds()) {
            BranchEntity branch = branchJpaRepository.findById(branchId)
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal", branchId));
            UserBranchEntity ub = new UserBranchEntity();
            ub.setUser(user);
            ub.setBranch(branch);
            userBranchJpaRepository.save(ub);
        }
    }

    @Override
    @Transactional
    public void removeBranch(Long userId, Long branchId) {
        UserEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", userId));
        BranchEntity branch = branchJpaRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", branchId));
        userBranchJpaRepository.deleteByUserAndBranch(user, branch);
    }

    @Override
    @Transactional
    public void changeOwnPassword(Long userId, ChangeOwnPasswordRequest request) {
        UserEntity entity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", userId));

        if (!passwordEncoder.matches(request.currentPassword(), entity.getPasswordHash())) {
            throw new UnauthorizedException("La contraseña actual es incorrecta");
        }

        entity.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userJpaRepository.save(entity);

        // Revoke all refresh tokens
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    @Override
    @Transactional
    public void adminChangePassword(Long userId, AdminChangePasswordRequest request) {
        UserEntity entity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", userId));

        entity.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userJpaRepository.save(entity);

        // Revoke all refresh tokens
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", userId));

        List<Role> roles = roleRepository.findByUserId(userId);
        List<Long> roleIds = roles.stream().map(Role::id).toList();
        List<Permission> permissions = permissionRepository.findByRoleIds(roleIds);

        List<String> roleNames = roles.stream().map(Role::name).toList();
        List<String> permissionCodes = permissions.stream()
                .map(Permission::code)
                .distinct()
                .toList();

        return userMapper.toProfileResponse(user, roleNames, permissionCodes);
    }

    private UserDetailResponse buildDetailResponse(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", userId));

        List<Role> roles = roleRepository.findByUserId(userId);
        List<RoleResponse> roleResponses = roles.stream()
                .map(r -> roleMapper.toResponse(r, 0, 0))
                .toList();

        // Get branches assigned to user
        UserEntity entity = userJpaRepository.findById(userId).orElseThrow();
        List<BranchResponse> branchResponses = userBranchJpaRepository.findByUser(entity).stream()
                .map(ub -> branchMapper.toDomain(ub.getBranch()))
                .map(branchMapper::toResponse)
                .toList();

        return userMapper.toDetailResponse(user, roleResponses, branchResponses);
    }
}
