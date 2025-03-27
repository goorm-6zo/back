package goorm.back.zo6.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import goorm.back.zo6.auth.application.OAuth2LoginSuccessHandlerFactory;
import goorm.back.zo6.auth.exception.CustomAccessDeniedHandler;
import goorm.back.zo6.auth.exception.CustomAuthenticationEntryPoint;
import goorm.back.zo6.auth.filter.JwtAuthFilter;
import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.user.application.OAuth2UserServiceFactory;
import goorm.back.zo6.user.domain.Role;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final OAuth2UserServiceFactory oAuth2UserServiceFactory;
    private final OAuth2LoginSuccessHandlerFactory successHandlerFactory;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //cors 설정
        http.cors((cors -> cors.configurationSource(configurationSource())));
        // csfr disable
        http.csrf((auth) -> auth.disable());
        // form 로그인 disable
        http.formLogin((auth) -> auth.disable());
        // HTTP Basic 인증 방식 disable
        http.httpBasic((auth) -> auth.disable());

        //경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**","/actuator/**").permitAll() // Swagger 관련 경로 허용
                .requestMatchers("/api/v1/users/signup","/api/v1/auth/login","/api/v1/users/check-email").permitAll()
                .requestMatchers("/api/v1/users/code","/api/v1/users/verify").permitAll()
                .requestMatchers("/api/v1/rekognition/authentication").permitAll()
                .requestMatchers("/api/v1/reservation/temp").permitAll()
                .requestMatchers("/conferences").permitAll()
                .requestMatchers("/sessions").permitAll()
                .requestMatchers("/api/v1/conference/**").permitAll()
                .requestMatchers("/api/v1/conferences/**").permitAll()
                .requestMatchers("/api/v1/face/authentication").permitAll()
                .requestMatchers("/api/v1/conferences/image/**").permitAll()
                .requestMatchers("/api/v1/face/authentication","/api/v1/face/collection").permitAll()
                .requestMatchers("/api/v1/redis").permitAll()
                .requestMatchers("/api/v1/sse/subscribe").permitAll()
                .requestMatchers("/api/v1/admin/signup").permitAll()
                .requestMatchers("/api/v1/admin/conference/**").hasRole(Role.ADMIN.getRoleName())
                .requestMatchers(HttpMethod.GET,"/api/v1/notices/**").permitAll()
                .requestMatchers("/api/v1/notices/**").hasRole(Role.ADMIN.getRoleName())
                .requestMatchers("/oauth2/**", "/auth/login/kakao/**").permitAll()
                .anyRequest().authenticated());

        //세션 설정 : STATELESS
        http.sessionManagement((session) -> session
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
                    OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;
                    String provider = oAuth2Token.getAuthorizedClientRegistrationId();

                    AuthenticationSuccessHandler handler = successHandlerFactory.getHandler(provider);
                    handler.onAuthenticationSuccess(request, response, authentication);
                })
        );
        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowedOrigins(Arrays.asList("https://server.maskpass.site", "http://localhost:5173", "https://maskpass-6zo.vercel.app","https://www.maskpass.site"));
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("ACCESS_TOKEN");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 주소요청에 위 설정을 넣어주겠다.
        return source;
    }
}