package goorm.back.zo6.reservation.application.shared;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserContext {

    private final UserRepository userRepository;

    public User findByEmailOrThrow(String email) {

        return userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public User findByPhoneOrThrow(String phone) {

        return userRepository.findByPhone(phone).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public Long getCurrentUserId() {

        String email = getCurrentUserEmail();
        User user = findByEmailOrThrow(email);
        return user.getId();
    }

    public String getCurrentUserEmail() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public String getCurrentUserName() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getName();
    }

    public String getCurrentUserPhone() {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByName(name).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getPhone();
    }
}
