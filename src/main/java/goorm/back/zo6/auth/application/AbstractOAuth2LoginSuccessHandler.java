package goorm.back.zo6.auth.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import goorm.back.zo6.auth.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract class AbstractOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    protected abstract String getEmail(OAuth2User oAuth2User);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = getEmail(oAuth2User);

        String accessToken = jwtUtil.generateToken(email);

        Cookie cookie = new Cookie("Authorization", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge((int) TimeUnit.HOURS.toSeconds(2));

        response.addCookie(cookie);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "OAuth2 로그인 성공");
        responseBody.put("email", email);
        responseBody.put("accessToken", accessToken);

        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
