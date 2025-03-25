package goorm.back.zo6.auth.application;

import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KakaoOAuth2LoginSuccessHandler extends AbstractOAuth2LoginSuccessHandler {

    private final UserRepository userRepository;

    public KakaoOAuth2LoginSuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        super(jwtUtil);
        this.userRepository = userRepository;
    }

    @Override
    protected String getEmail(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakakAccount = (Map<String, Object>) attributes.get("kakao_account");
        return (String) kakakAccount.get("email");
    }

    @Override
    protected Long getUserId(OAuth2User oAuth2User) {
        User user = getUserData(oAuth2User);

        return user.getId();
    }

    @Override
    protected Role getRole(OAuth2User oAuth2User) {
        User user = getUserData(oAuth2User);

        return user.getRole();
    }

    private User getUserData(OAuth2User oAuth2User) {
        String email = getEmail(oAuth2User);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
