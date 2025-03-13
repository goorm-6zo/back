package goorm.back.zo6.user.application;

public interface UserService {
    UserResponse findById(Long userId);
    SignUpResponse signUp(SignUpRequest request);
    UserResponse findByToken(String email);
    void delete(String email);
    UserResponse addPhoneNumber(String email, String phoneNumber);
}
