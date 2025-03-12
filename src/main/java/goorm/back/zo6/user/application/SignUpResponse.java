package goorm.back.zo6.user.application;

import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SignUpResponse(
        Long id,
        String email,
        String phone,
        String name,
        Role role,
        LocalDateTime createdAt
) {
    public static SignUpResponse from(User user){
        return SignUpResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
