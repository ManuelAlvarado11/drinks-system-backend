package drinks.system.accessservice.infrastructure.adapter.in.rest;

import drinks.system.accessservice.application.dto.request.LoginRequest;
import drinks.system.accessservice.application.dto.request.LogoutRequest;
import drinks.system.accessservice.application.dto.request.RefreshTokenRequest;
import drinks.system.accessservice.application.dto.request.SwitchBranchRequest;
import drinks.system.accessservice.application.dto.response.AuthResponse;
import drinks.system.accessservice.domain.port.in.AuthUseCase;
import drinks.system.common.dto.ApiResponse;
import drinks.system.common.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/access/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = extractIpAddress(httpRequest);
        AuthResponse response = authUseCase.login(request, ipAddress);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        AuthResponse response = authUseCase.refresh(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody LogoutRequest request) {

        authUseCase.logout(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Sesión cerrada exitosamente"));
    }

    @PostMapping("/switch-branch")
    public ResponseEntity<ApiResponse<AuthResponse>> switchBranch(
            @Valid @RequestBody SwitchBranchRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest httpRequest) {

        String ipAddress = extractIpAddress(httpRequest);
        AuthResponse response = authUseCase.switchBranch(request, principal.userId(), ipAddress);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private String extractIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
