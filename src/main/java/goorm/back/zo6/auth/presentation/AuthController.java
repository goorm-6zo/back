package goorm.back.zo6.auth.presentation;

import goorm.back.zo6.auth.application.AuthService;
import goorm.back.zo6.auth.domain.LoginUser;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 입력받아 JWT 토큰을 발급합니다.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        ResponseCookie cookie = CookieUtil.createCookie(COOKIE_NAME, loginResponse.accessToken(), TOKEN_VALID_TIME);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }

    @DeleteMapping("/logout")
    @Operation(summary = "로그아웃", description = "쿠키를 삭제하여 로그아웃합니다.")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal LoginUser loginUser){
        ResponseCookie cookie = CookieUtil.deleteCookie(COOKIE_NAME);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message","로그아웃 성공!"));
    }

}
