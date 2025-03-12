package goorm.back.zo6.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2UserServiceFactory {
    private final KakaoOAuth2UserServiceImpl kakaoOAuth2UserService;
    private final NaverOAuth2UserServiceImpl naverOAuth2UserService;

    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        return switch (registrationId.toLowerCase()) {
            case "kakao" -> kakaoOAuth2UserService.loadUser(userRequest);
            case "naver" -> naverOAuth2UserService.loadUser(userRequest);
            default -> throw new IllegalStateException("Unexpected value: " + registrationId);
        };
    }
}
