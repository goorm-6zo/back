package goorm.back.zo6.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import goorm.back.zo6.auth.exception.CustomAccessDeniedHandler;
import goorm.back.zo6.auth.exception.CustomAuthenticationEntryPoint;
import goorm.back.zo6.auth.filter.JwtAuthFilter;
import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.auth.application.OAuth2LoginSuccessHandlerFactory;
import goorm.back.zo6.user.application.OAuth2UserServiceFactory;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final OAuth2UserServiceFactory oAuth2UserServiceFactory;
    private final OAuth2LoginSuccessHandlerFactory successHandlerFactory;

    @Value("${server.url}")
    private String SERVER_URL;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CORS 설정
        http.cors(cors -> cors.configurationSource(configurationSource()));

        // CSRF 비활성화
        http.csrf(csrf -> csrf.disable());

        // Form 로그인 비활성화
        http.formLogin(form -> form.disable());

        // HTTP Basic 인증 비활성화
        http.httpBasic(httpBasic -> httpBasic.disable());

        // 경로별 인가 작업
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**", "/actuator/**").permitAll() // Swagger 관련 경로 허용
                .requestMatchers("/oauth2/**", "/auth/login/kakao/**", "/auth/login/naver/**").permitAll() // OAuth2 관련 경로 허용
                .requestMatchers("/api/v1/users/signup", "/api/v1/auth/login").permitAll()
                .requestMatchers("/api/v1/rekognition/authentication").permitAll()
                .anyRequest().authenticated());

        // 세션 설정 : STATELESS
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // JWTFilter 추가
        http.addFilterBefore(new JwtAuthFilter(jwtUtil, objectMapper), UsernamePasswordAuthenticationFilter.class);

        // Exception handler 추가
        http.exceptionHandling(exceptionHandling ->
                exceptionHandling
                        .accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper))
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper)));

        // OAuth2 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserServiceFactory::loadUser))
                        .successHandler((request, response, authentication) -> {
                            if (authentication instanceof OAuth2AuthenticationToken oAuth2Token) {
                                String provider = oAuth2Token.getAuthorizedClientRegistrationId().toUpperCase();
                                log.debug("OAuth2 Provider: {}", provider);

                                AuthenticationSuccessHandler handler = successHandlerFactory.getHandler(provider);
                                handler.onAuthenticationSuccess(request, response, authentication);
                            } else {
                                log.error("OAuth2 Authentication Token이 아닙니다: {}", authentication.getClass().getName());
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid authentication token");
                            }
                        })
                );
        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOriginPattern(SERVER_URL);
        configuration.addAllowedOrigin("http://localhost:5173");  // 특정 도메인 허용
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("ACCESS_TOKEN");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 요청에 적용
        return source;
    }
}
