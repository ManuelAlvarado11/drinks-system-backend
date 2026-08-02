package drinks.system.accessservice.domain.port.in;

import drinks.system.accessservice.application.dto.request.LoginRequest;
import drinks.system.accessservice.application.dto.request.LogoutRequest;
import drinks.system.accessservice.application.dto.request.RefreshTokenRequest;
import drinks.system.accessservice.application.dto.response.AuthResponse;

/**
 * Puerto de entrada para operaciones de autenticación.
 * Define los casos de uso de login, renovación de token y logout.
 */
public interface AuthUseCase {

    AuthResponse login(LoginRequest request, String ipAddress);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(LogoutRequest request);
}
