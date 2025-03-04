package goorm.back.zo6.user.dto.request;

public record LoginRequest(
    String email,
    String password
){}
