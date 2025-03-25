package goorm.back.zo6.user.dto.response;

import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import lombok.Builder;

@Builder
public record UserResponse(
    Long id,
    String email,
    String name,
    String phone,
    Boolean isDeleted,
    Role role,
    Boolean hasFace
)
{
    public static UserResponse from(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phone(user.getPhone())
                .role(user.getRole())
                .isDeleted(user.getIsDeleted())
                .hasFace(user.getHasFace())
                .build();
    }
}
