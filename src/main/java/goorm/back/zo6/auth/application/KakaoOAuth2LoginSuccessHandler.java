package goorm.back.zo6.auth.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import goorm.back.zo6.auth.util.JwtUtil;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KakaoOAuth2LoginSuccessHandler extends AbstractOAuth2LoginSuccessHandler {

    public KakaoOAuth2LoginSuccessHandler(JwtUtil jwtUtil, ObjectMapper objectMapper) {
        super(jwtUtil, objectMapper);
    }

    @Override
    protected String getEmail(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakakAccount = (Map<String, Object>) attributes.get("kakao_account");
        return (String) kakakAccount.get("email");
    }
}
