package goorm.back.zo6.user.dto.response;

import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import lombok.Builder;

@Builder
public record UserResponse(
    Long id,
    String email,
    String phone,
    String birthDate,
    Boolean isDeleted,
    Role role
)
{
    public static UserResponse from(User user){
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .birthDate(user.getBirthDate())
                .isDeleted(user.getIsDeleted())
                .role(user.getRole())
                .build();
    }
}
