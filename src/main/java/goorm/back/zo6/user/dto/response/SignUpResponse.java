package goorm.back.zo6.user.dto.response;

import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SignUpResponse(
        Long userId,
        String email,
        String phone,
        String name,
        Role role,
        LocalDateTime createdAt
) {
    public static SignUpResponse from(User user){
        return SignUpResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .name(user.getName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
