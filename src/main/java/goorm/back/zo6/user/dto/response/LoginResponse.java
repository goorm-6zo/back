package goorm.back.zo6.user.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken
) {
}
