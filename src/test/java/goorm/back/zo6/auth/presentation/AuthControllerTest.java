package goorm.back.zo6.auth.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import goorm.back.zo6.auth.application.AuthService;
import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.config.RestDocsConfiguration;
import goorm.back.zo6.user.domain.Password;
import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import goorm.back.zo6.user.application.LoginRequest;
import goorm.back.zo6.user.infrastructure.UserJpaRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(RestDocumentationExtension.class)
@Import(RestDocsConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestDocumentationResultHandler restDocs;

    @Autowired
    private WebApplicationContext context;

    private User testUser;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        // Rest Docs 설정
        this.mockMvc = webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(restDocs)
                .build();

        testUser = User.builder()
                .name("홍길순")
                .email("test@gmail.com")
                .phone("01011112222")
                .password(Password.from(passwordEncoder.encode("1234")))
                .role(Role.of("USER"))
                .build();
        userJpaRepository.saveAndFlush(testUser);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
    }


    @Test
    @DisplayName("유저가 로그인을 성공적으로 완료하여 토큰을 발급받습니다.")
    void loginTest_Success() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest("test@gmail.com", "1234");
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        // when && then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(
                        status().isOk(),
                        header().exists(HttpHeaders.SET_COOKIE),
                        header().string(HttpHeaders.SET_COOKIE, Matchers.containsString("Authorization=")),
                        jsonPath("$.message").value("로그인 성공!")
                );
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인을 시도하면 400 BadRequest 가 반환됩니다.")
    void loginTest_Fail_WrongPassword() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest("test@gmail.com", "wrongpassword");
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        // when && then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()),
                        jsonPath("$.message").value("로그인 정보에 해당하는 유저가 존재하지 않습니다.")
                );
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인을 시도하면 400 BadRequest 가 반환됩니다.")
    void loginTest_Fail_NonExistentEmail() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest("nonexistent@gmail.com", "1234");
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        // when && then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(
                        jsonPath("$.status").value(HttpStatus.BAD_REQUEST.name()),
                        jsonPath("$.message").value("로그인 정보에 해당하는 유저가 존재하지 않습니다.")
                );
    }
}