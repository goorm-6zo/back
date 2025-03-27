package goorm.back.zo6.user.application;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final PhoneValidService phoneValidService;

    private final UserRepository userRepository;

    public void validatePhone(String phone) {
        if (!phoneValidService.isPhoneAlreadyVerified(phone)) {
            throw new CustomException(ErrorCode.PHONE_NOT_VERIFIED);
        }
    }
}
