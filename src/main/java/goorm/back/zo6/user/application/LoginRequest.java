package goorm.back.zo6.user.application;

public record LoginRequest(
    String email,
    String password
){}
