package goorm.back.zo6.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import goorm.back.zo6.auth.domain.LoginUser;
import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static goorm.back.zo6.common.exception.ErrorCode.UNKNOWN_TOKEN_ERROR;


@Log4j2
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = findToken(request);

            if (!verifyToken(request, token)) {
                filterChain.doFilter(request, response);
                return;
            }

            User user = getUser(token);
            setSecuritySession(user);
            filterChain.doFilter(request, response);

        }catch (CustomException e){
            ErrorCode errorCode =  e.getErrorCode();
            switch (errorCode) {
                case WRONG_TYPE_TOKEN, UNSUPPORTED_TOKEN, EXPIRED_TOKEN, UNKNOWN_TOKEN_ERROR ->
                        setResponse(response, errorCode);
                default -> {
                    log.error("알 수 없는 에러 코드: {}", errorCode);
                    setResponse(response, UNKNOWN_TOKEN_ERROR); // 기본 예외 처리
                }
            }
        }
    }

    private static void setSecuritySession(User user){
        LoginUser loginUser = new LoginUser(user);
        log.info("SessionLoginUser : {}", loginUser.getUsername());
        Authentication authToken = new UsernamePasswordAuthenticationToken(loginUser,null, null);
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private User getUser(String token){
        Long userId = jwtUtil.getUserId(token);
        String email = jwtUtil.getUsername(token);
        String name = jwtUtil.getName(token);
        String role = jwtUtil.getRole(token);

        log.info("getUser username: ", email);
        return User.builder()
                .id(userId)
                .email(email)
                .name(name)
                .role(Role.of(role))
                .build();
    }

    private boolean verifyToken(HttpServletRequest request,String token) throws IOException, ServletException {
        Boolean isValid = (Boolean) request.getAttribute("isTokenValid");
        if(isValid != null) return isValid;

        if (token == null || jwtUtil.validateToken(token)) {
            log.debug("token null");
            request.setAttribute("isTokenValid",false);
            return false;
        }

        request.setAttribute("isTokenValid", true);
        return true;
    }

    private static String findToken(HttpServletRequest request){
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

    private void setResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getStatus().value());
        response.getWriter().print(objectMapper.writeValueAsString(errorCode.getMessage()));
    }
}
