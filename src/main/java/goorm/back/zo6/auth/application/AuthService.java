package goorm.back.zo6.auth.application;

import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import goorm.back.zo6.user.application.LoginRequest;
import goorm.back.zo6.user.application.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest){
        User user = userRepository.findByEmail(loginRequest.email())
                .filter(m -> passwordEncoder.matches(loginRequest.password(),m.getPassword().getValue()))
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_MATCH_LOGIN_INFO));

        String accessToken = jwtUtil.createAccessToken(user.getEmail());
        return LoginResponse.builder().accessToken(accessToken).build();
    }
}
