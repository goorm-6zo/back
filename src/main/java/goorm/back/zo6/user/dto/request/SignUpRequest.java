package goorm.back.zo6.user.dto.request;

public record SignUpRequest(
        String name,
        String email,
        String password,
        String phone
) {
}
