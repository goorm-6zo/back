package goorm.back.zo6.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.user.application.UserService;
import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import goorm.back.zo6.user.dto.request.SignUpRequest;
import goorm.back.zo6.user.infrastructure.UserJpaRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp(){
        testUser = User.builder()
                .name("홍길순")
                .email("test@gmail.com")
                .phone("01011112222")
                .role(Role.of("USER"))
                .build();
        userJpaRepository.saveAndFlush(testUser);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
    }

    private String generateTestToken(User user) {
        return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole());
    }

    @Test
    @DisplayName("유저 id로 유저 정보 조회 성공 통합 테스트")
    void getUserById_Success() throws Exception {
        // given
        String testToken = generateTestToken(testUser);
        Long userId = testUser.getId();

        // when && then
        mockMvc.perform(get("/api/v1/users/{userId}", userId)
                        .cookie(new Cookie("Authorization", testToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.phone").value(testUser.getPhone()))
                .andExpect(jsonPath("$.name").value(testUser.getName()))
                .andExpect(jsonPath("$.role").value(testUser.getRole().getRoleName()));
    }

    @Test
    @DisplayName("유저 토큰으로 조회 성공 통합 테스트")
    void findByToken_Success() throws Exception {
        // given
        String testToken = generateTestToken(testUser);

        // when && then
        mockMvc.perform(get("/api/v1/users")
                        .cookie(new Cookie("Authorization", testToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.phone").value(testUser.getPhone()))
                .andExpect(jsonPath("$.name").value(testUser.getName()))
                .andExpect(jsonPath("$.role").value(testUser.getRole().getRoleName()));
    }

    @Test
    @DisplayName("유저 회원가입 - 성공")
    void signUp_Success() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("홍길동","test@naver.com","4321", "010-1234-5678");

        // when & then
        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.role").exists())
                .andExpect(jsonPath("$.email").value(request.email()))
                .andExpect(jsonPath("$.phone").value(request.phone()))
                .andExpect(jsonPath("$.name").value(request.name()));
    }

    @Test
    @DisplayName("유저 회원가입 - 이름이 비어있을 때 실패")
    void signUp_NameBlankFails() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("", "test@naver.com", "4321", "01012345678");

        // when & then
        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("잘못된 요청입니다."),
                        jsonPath("$.validationErrors[0].field").value("name"),
                        jsonPath("$.validationErrors[0].message").value("이름을 입력해 주세요.")
                );
    }

    @Test
    @DisplayName("유저 회원가입 - 이메일이 비어있을 때 실패")
    void signUp_EmailBlankFails() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("홍길동", "", "4321", "01012345678");

        // when & then
        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("잘못된 요청입니다."),
                        jsonPath("$.validationErrors[0].field").value("email"),
                        jsonPath("$.validationErrors[0].message").value("이메일을 입력해 주세요.")
                );
    }

    @Test
    @DisplayName("유저 회원가입 - 비밀번호가 비어있을 때 실패")
    void signUp_PasswordBlankFails() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("홍길동", "test@naver.com", "", "01012345678");

        // when & then
        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("잘못된 요청입니다."),
                        jsonPath("$.validationErrors[0].field").value("password"),
                        jsonPath("$.validationErrors[0].message").value("비밀번호를 입력해 주세요.")
                );
    }

    @Test
    @DisplayName("유저 회원가입 - 전화번호가 비어있을 때 실패")
    void signUp_PhoneBlankFails() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("홍길동", "test@naver.com", "4321", "");

        // when & then
        mockMvc.perform(post("/api/v1/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.status").value("BAD_REQUEST"),
                        jsonPath("$.message").value("잘못된 요청입니다."),
                        jsonPath("$.validationErrors[0].field").value("phone"),
                        jsonPath("$.validationErrors[0].message").value("전화 번호를 입력해 주세요.")
                );
    }
    
    @Test
    @DisplayName("토큰 기반 유저 회원 탈퇴 성공 테스트.")
    void deactivateUser_Success() throws Exception {
        // given
        String email = testUser.getEmail();
        String testToken = generateTestToken(testUser);

        // when && then
        mockMvc.perform(delete("/api/v1/users")
                        .cookie(new Cookie("Authorization", testToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("성공적으로 회원 탈퇴하였습니다."));

        // 데이터베이스에서 삭제 확인
        Optional<User> user = userRepository.findByEmail(email);
        assertTrue(user.isEmpty());
    }
}