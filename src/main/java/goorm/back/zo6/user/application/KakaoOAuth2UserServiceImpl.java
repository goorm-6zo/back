package goorm.back.zo6.user.application;

import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoOAuth2UserServiceImpl extends DefaultOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.getOrDefault("kakao_account", Map.of());
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.getOrDefault("profile", Map.of());

        String email = (String) kakaoAccount.getOrDefault("email", null);
        String nickname = (String) profile.getOrDefault("nickname", "Unknown");

        User user = userRepository.findByEmail(email)
                .map(existingUser -> {
                    existingUser.updateNickname(nickname);
                    return existingUser;
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .name(nickname)
                            .password(null)
                            .phone(null)
                            .role(Role.USER)
                            .build();
                    return userRepository.save(newUser);
                });

        return oAuth2User;
    }
}
