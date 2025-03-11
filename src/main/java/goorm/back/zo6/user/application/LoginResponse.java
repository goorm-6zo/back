package goorm.back.zo6.user.application;

import lombok.Builder;

@Builder
public record LoginResponse(
        String accessToken
) {
}
