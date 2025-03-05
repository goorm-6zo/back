package goorm.back.zo6.admin.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.user.application.UserService;
import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import goorm.back.zo6.user.infrastructure.UserJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class QRCodeControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private UserService userService;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private EntityManager entityManager;

    private User testUser;
    private String testToken;

    @BeforeEach
    void setUp(){
        // (컨퍼런스, 세션)테이블, 더미데이터 생성 쿼리 (로직 구현시 제거후 수정예정)
        entityManager.createNativeQuery("""
        CREATE TABLE conference (
            conference_id BIGINT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            has_sessions BOOLEAN NOT NULL
        )
    """).executeUpdate();
        entityManager.createNativeQuery("""
        CREATE TABLE session (
            session_id BIGINT AUTO_INCREMENT PRIMARY KEY,
            conference_id BIGINT NOT NULL,
            name VARCHAR(255) NOT NULL,
            capacity INT NOT NULL,
            location VARCHAR(255) NOT NULL,
            time TIMESTAMP NOT NULL,
            summary VARCHAR(255) NOT NULL,
            CONSTRAINT fk_conference FOREIGN KEY (conference_id) REFERENCES conference(conference_id)
        )
    """).executeUpdate();

        entityManager.createNativeQuery("INSERT INTO conference (name, has_sessions) VALUES (:name, :hasSessions)")
                .setParameter("name", "테스트 컨퍼런스")
                .setParameter("hasSessions", true)
                .executeUpdate();

        entityManager.createNativeQuery("""
        INSERT INTO session (conference_id, name, capacity, location, time, summary)
        VALUES (1, '테스트 세션', 100, '서울', '2024-04-01T10:00:00', '테스트 세션입니다.')
    """).executeUpdate();

        testUser = User.builder()
                .name("홍길순")
                .email("test@gmail.com")
                .phone("01011112222")
                .birthDate("2000-10-20")
                .role(Role.of("USER"))
                .build();
        userJpaRepository.saveAndFlush(testUser);

        testToken = generateTestToken(testUser);
    }

    @AfterEach
    @Transactional
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
        entityManager.createNativeQuery("DROP TABLE session").executeUpdate();
        entityManager.createNativeQuery("DROP TABLE conference").executeUpdate();
    }

    @Test
    @DisplayName("QR 코드 생성 성공")
    void QRCode_Create_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/qr")
                        .cookie(new Cookie("Authorization", testToken))
                        .param("conferenceId", "1")
                        .param("sessionId", "1")
                        .param("url", "https://www.google.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("QR 코드 생성 실패 - ConferenceId NOT FOUND")
    void QRCode_ConferenceId_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/admin/qr")
                .cookie(new Cookie("Authorization", testToken))
                .param("conferenceId", "2")
                .param("sessionId", "1")
                .param("url", "https://www.google.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.code").value(HttpStatus.NOT_FOUND.name()),
                        jsonPath("$.message").value("존재하지 않는 컨퍼런스입니다.")
                );
    }

    @Test
    @DisplayName("QR 코드 생성 실패 - SessionId NOT FOUND")
    void QRCode_SessionId_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/admin/qr")
                        .cookie(new Cookie("Authorization", testToken))
                        .param("conferenceId", "1")
                        .param("sessionId", "2")
                        .param("url", "https://www.google.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.code").value(HttpStatus.NOT_FOUND.name()),
                        jsonPath("$.message").value("존재하지 않는 세션입니다.")
                );
    }

    private String generateTestToken(User user) {
        return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }
}
