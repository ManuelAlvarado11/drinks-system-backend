package drinks.system.accessservice.application.service;

import drinks.system.accessservice.application.dto.request.LoginRequest;
import drinks.system.accessservice.application.dto.request.LogoutRequest;
import drinks.system.accessservice.application.dto.request.RefreshTokenRequest;
import drinks.system.accessservice.application.dto.request.SwitchBranchRequest;
import drinks.system.accessservice.application.dto.response.AuthResponse;
import drinks.system.accessservice.application.dto.response.UserProfileResponse;
import drinks.system.accessservice.application.mapper.UserMapper;
import drinks.system.accessservice.domain.model.Permission;
import drinks.system.accessservice.domain.model.RefreshToken;
import drinks.system.accessservice.domain.model.Role;
import drinks.system.accessservice.domain.model.User;
import drinks.system.accessservice.domain.port.in.AuthUseCase;
import drinks.system.accessservice.domain.port.out.PermissionRepositoryPort;
import drinks.system.accessservice.domain.port.out.RefreshTokenRepositoryPort;
import drinks.system.accessservice.domain.port.out.RoleRepositoryPort;
import drinks.system.accessservice.domain.port.out.UserRepositoryPort;
import drinks.system.common.audit.AuditEvent;
import drinks.system.common.exception.UnauthorizedException;
import drinks.system.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${security.jwt.expiration-minutes:15}")
    private long expirationMinutes;

    @Value("${security.jwt.refresh-token-expiration-days:7}")
    private long refreshTokenExpirationDays;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        if (!user.isActive()) {
            throw new UnauthorizedException("La cuenta está desactivada");
        }

        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        // Get roles and permissions
        List<Role> roles = roleRepository.findByUserId(user.id());
        List<Long> roleIds = roles.stream().map(Role::id).toList();
        List<Permission> permissions = permissionRepository.findByRoleIds(roleIds);
        List<String> permissionCodes = permissions.stream()
                .map(Permission::code)
                .distinct()
                .toList();

        // Generate JWT
        String accessToken = jwtTokenProvider.generateToken(
                user.id().toString(),
                user.username(),
                user.branchId(),
                permissionCodes
        );

        // Generate refresh token
        String rawRefreshToken = UUID.randomUUID().toString();
        String tokenHash = hashToken(rawRefreshToken);

        RefreshToken refreshToken = new RefreshToken(
                null,
                user.id(),
                tokenHash,
                Instant.now().plus(refreshTokenExpirationDays, ChronoUnit.DAYS),
                false,
                ipAddress,
                null
        );
        refreshTokenRepository.save(refreshToken);

        // Update last login
        userRepository.updateLastLogin(user.id(), Instant.now());

        // Build profile response
        List<String> roleNames = roles.stream().map(Role::name).toList();
        UserProfileResponse profile = userMapper.toProfileResponse(user, roleNames, permissionCodes);

        // Publish audit event
        eventPublisher.publishEvent(new AuditEvent(
                user.id(), user.username(), "LOGIN", "ACCESS",
                "User", user.id(), null, null, ipAddress,
                "Inicio de sesión exitoso"
        ));

        return AuthResponse.of(accessToken, rawRefreshToken, expirationMinutes * 60, profile);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String tokenHash = hashToken(request.refreshToken());

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido"));

        // Theft detection: if token already revoked, revoke all user tokens
        if (storedToken.isRevoked()) {
            refreshTokenRepository.revokeAllByUserId(storedToken.userId());
            throw new UnauthorizedException("Reuso de token detectado. Todas las sesiones han sido revocadas");
        }

        // Check expiration
        if (storedToken.expiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expirado");
        }

        // Revoke old token (rotation)
        refreshTokenRepository.revokeByTokenHash(tokenHash);

        // Get user, roles, permissions
        User user = userRepository.findById(storedToken.userId())
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        if (!user.isActive()) {
            throw new UnauthorizedException("La cuenta está desactivada");
        }

        List<Role> roles = roleRepository.findByUserId(user.id());
        List<Long> roleIds = roles.stream().map(Role::id).toList();
        List<Permission> permissions = permissionRepository.findByRoleIds(roleIds);
        List<String> permissionCodes = permissions.stream()
                .map(Permission::code)
                .distinct()
                .toList();

        // Generate new JWT
        String accessToken = jwtTokenProvider.generateToken(
                user.id().toString(),
                user.username(),
                user.branchId(),
                permissionCodes
        );

        // Generate new refresh token
        String rawRefreshToken = UUID.randomUUID().toString();
        String newTokenHash = hashToken(rawRefreshToken);

        RefreshToken newRefreshToken = new RefreshToken(
                null,
                user.id(),
                newTokenHash,
                Instant.now().plus(refreshTokenExpirationDays, ChronoUnit.DAYS),
                false,
                storedToken.deviceInfo(),
                null
        );
        refreshTokenRepository.save(newRefreshToken);

        List<String> roleNames = roles.stream().map(Role::name).toList();
        UserProfileResponse profile = userMapper.toProfileResponse(user, roleNames, permissionCodes);

        return AuthResponse.of(accessToken, rawRefreshToken, expirationMinutes * 60, profile);
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) {
        String tokenHash = hashToken(request.refreshToken());
        refreshTokenRepository.revokeByTokenHash(tokenHash);
    }

    @Override
    @Transactional
    public AuthResponse switchBranch(SwitchBranchRequest request, Long userId, String ipAddress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

        if (!user.isActive()) {
            throw new UnauthorizedException("La cuenta está desactivada");
        }

        // Validate user is authorized for the target branch
        Long targetBranchId = request.branchId();
        boolean authorized = user.branches() != null &&
                user.branches().stream().anyMatch(b -> b.id().equals(targetBranchId));

        if (!authorized) {
            throw new UnauthorizedException("No tiene acceso a la sucursal seleccionada");
        }

        // Update user's active branch
        User updatedUser = new User(user.id(), user.username(), user.passwordHash(), user.email(),
                user.fullName(), targetBranchId, user.isActive(), user.lastLogin(), user.deletedAt(),
                user.createdAt(), user.updatedAt(), user.createdBy(), user.updatedBy(),
                user.roles(), user.branches());
        userRepository.save(updatedUser);

        // Generate new tokens with updated branchId
        List<Role> roles = roleRepository.findByUserId(user.id());
        List<Long> roleIds = roles.stream().map(Role::id).toList();
        List<Permission> permissions = permissionRepository.findByRoleIds(roleIds);
        List<String> permissionCodes = permissions.stream()
                .map(Permission::code)
                .distinct()
                .toList();

        String accessToken = jwtTokenProvider.generateToken(
                user.id().toString(),
                user.username(),
                targetBranchId,
                permissionCodes
        );

        String rawRefreshToken = UUID.randomUUID().toString();
        String tokenHash = hashToken(rawRefreshToken);

        RefreshToken refreshToken = new RefreshToken(
                null,
                user.id(),
                tokenHash,
                Instant.now().plus(refreshTokenExpirationDays, ChronoUnit.DAYS),
                false,
                ipAddress,
                null
        );
        refreshTokenRepository.save(refreshToken);

        List<String> roleNames = roles.stream().map(Role::name).toList();
        UserProfileResponse profile = userMapper.toProfileResponse(updatedUser, roleNames, permissionCodes);

        eventPublisher.publishEvent(new AuditEvent(
                user.id(), user.username(), "SWITCH_BRANCH", "ACCESS",
                "User", user.id(), null, null, ipAddress,
                "Cambio de sucursal a branchId=" + targetBranchId
        ));

        return AuthResponse.of(accessToken, rawRefreshToken, expirationMinutes * 60, profile);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
