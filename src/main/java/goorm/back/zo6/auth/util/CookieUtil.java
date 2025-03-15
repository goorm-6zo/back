package goorm.back.zo6.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final EncryptionUtils encryptionUtils;

    public ResponseCookie createCookie(String name, String value, long cookieExpiration) {
        String encrypt = encryptionUtils.encrypt(String.valueOf(value));
        return ResponseCookie.from(name, encrypt)
                .maxAge(cookieExpiration)
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .build();
    }

    public ResponseCookie deleteCookie(String name){
        return ResponseCookie.from(name, null)
                .maxAge(0)
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .build();
    }

    public String getDecodedCookieValue(String cookieValue){
        return encryptionUtils.decrypt(cookieValue);
    }

    public String findToken(HttpServletRequest request){
        String token = null;
        Cookie[] cookies = request.getCookies();

        if(cookies == null){
            return null;
        }

        for(Cookie cookie : cookies){
            if(cookie.getName().equals("Authorization")){
                token = cookie.getValue();
            }
        }
        return token;
    }

}
