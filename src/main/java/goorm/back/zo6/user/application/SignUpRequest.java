package goorm.back.zo6.user.application;

public record SignUpRequest(
        String name,
        String email,
        String password,
        String phone
) {
}
