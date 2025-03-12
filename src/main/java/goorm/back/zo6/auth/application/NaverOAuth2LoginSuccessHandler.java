package goorm.back.zo6.auth.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import goorm.back.zo6.auth.util.JwtUtil;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NaverOAuth2LoginSuccessHandler extends AbstractOAuth2LoginSuccessHandler{

    public NaverOAuth2LoginSuccessHandler(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        super(jwtUtil, objectMapper);
    }

    @Override
    protected String getEmail(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            throw new IllegalArgumentException("네이버 OAuth2 응답에서 response 데이터가 없습니다.");
        }

        return (String) response.get("email");
    }
}
