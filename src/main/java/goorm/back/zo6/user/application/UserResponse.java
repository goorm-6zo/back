package goorm.back.zo6.user.application;

import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import lombok.Builder;

@Builder
public record UserResponse(
    Long id,
    String email,
    String name,
    Boolean isDeleted,
    Role role
)
{
    public static UserResponse from(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .isDeleted(user.getIsDeleted())
                .role(user.getRole())
                .build();
    }
}
