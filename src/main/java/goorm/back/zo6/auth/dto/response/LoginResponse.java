package goorm.back.zo6.auth.dto.response;

import goorm.back.zo6.user.domain.Role;
import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken,
        Long id,
        String email,
        String name,
        String phone,
        Role role
) {
}
