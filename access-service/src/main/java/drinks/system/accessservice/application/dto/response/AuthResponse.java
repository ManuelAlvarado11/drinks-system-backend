package drinks.system.accessservice.application.dto.response;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        UserProfileResponse user
) {
    public static AuthResponse of(String accessToken, String refreshToken, long expiresInSeconds, UserProfileResponse user) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresInSeconds, user);
    }
}
