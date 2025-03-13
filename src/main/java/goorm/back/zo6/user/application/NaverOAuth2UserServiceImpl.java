package goorm.back.zo6.user.application;

import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NaverOAuth2UserServiceImpl extends DefaultOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        String id = (String) response.get("id");
        if (id == null) {
            throw new IllegalArgumentException("네이버 OAuth 응답에서 'id' 값이 없습니다.");
        }

        String email = (String) response.getOrDefault("email", "unknown@naver.com");
        String nickname = (String) response.getOrDefault("nickname", "Unknown");

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

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                response,
                "id"
        );
    }
}
