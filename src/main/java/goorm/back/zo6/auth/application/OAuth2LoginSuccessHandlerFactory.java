package goorm.back.zo6.auth.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandlerFactory {

    private final KakaoOAuth2LoginSuccessHandler kakaoHandler;
    private final NaverOAuth2LoginSuccessHandler naverHandler;

    public AuthenticationSuccessHandler getHandler(String provider) {
        return switch (provider.toUpperCase()) {
            case "KAKAO" -> kakaoHandler;
            case "NAVER" -> naverHandler;
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth2 Provider: " + provider);
        };
    }

    private String getRegistrationId(Map<String, Object> attributes) {
        if (attributes.containsKey("kakao_account")) {
            return "kakao";
        } else if (attributes.containsKey("response")) {
            return "naver";
        }
        return "unknown";
    }
}
