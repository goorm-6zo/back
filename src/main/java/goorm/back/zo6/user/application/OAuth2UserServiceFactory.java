package goorm.back.zo6.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceFactory {

    private final KakaoOAuth2UserServiceImpl kakaoOAuth2UserService;

    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if ("kakao".equalsIgnoreCase(registrationId)) {
            return kakaoOAuth2UserService.loadUser(userRequest);
        }

        throw new IllegalStateException("지원하지 않는 OAuth2 Provider: " + registrationId);
    }
}
