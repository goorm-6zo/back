package goorm.back.zo6.auth.presentation;

import goorm.back.zo6.auth.application.AuthService;
import goorm.back.zo6.auth.util.CookieUtil;
import goorm.back.zo6.user.dto.request.LoginRequest;
import goorm.back.zo6.user.dto.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Tag(name = "Auth", description = "Authorization API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${cookie.valid-time}")
    private long TOKEN_VALID_TIME;

    @Value("${cookie.name}")
    private String COOKIE_NAME;

    private final AuthService authService;

    public static final String BEARER_PREFIX = "Bearer ";

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 입력받아 JWT 토큰을 발급합니다.")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        String encodedToken = Base64.getUrlEncoder().encodeToString((BEARER_PREFIX + loginResponse.accessToken()).getBytes(StandardCharsets.UTF_8));
        ResponseCookie cookie = CookieUtil.createCookie(COOKIE_NAME, encodedToken, TOKEN_VALID_TIME);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body("로그인 성공!");
    }
}
