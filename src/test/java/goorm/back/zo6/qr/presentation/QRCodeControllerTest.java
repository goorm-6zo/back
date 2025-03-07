package goorm.back.zo6.qr.presentation;

import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.infrastructure.ConferenceJpaRepository;
import goorm.back.zo6.conference.infrastructure.SessionJpaRepository;
import goorm.back.zo6.config.RestDocsConfiguration;
import goorm.back.zo6.fixture.ConferenceFixture;
import goorm.back.zo6.fixture.SessionFixture;
import goorm.back.zo6.fixture.UserFixture;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.infrastructure.UserJpaRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
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
public class QRCodeControllerTest {

    @Autowired private WebApplicationContext context;
    private MockMvc mockMvc;
    @Autowired private UserJpaRepository userJpaRepository;
    @Autowired private ConferenceJpaRepository conferenceJpaRepository;
    @Autowired private SessionJpaRepository sessionJpaRepository;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private RestDocumentationResultHandler restDocs;

    private String testToken;
    private Long conferenceId;
    private Long sessionId;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        // Rest Docs 설정
        this.mockMvc = webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(restDocs)
                .build();

        User testUser = userJpaRepository.saveAndFlush(UserFixture.유저());
        Conference testConference = conferenceJpaRepository.saveAndFlush(ConferenceFixture.컨퍼런스());
        Session testSession = SessionFixture.세션(testConference);

        testConference.getSessions().add(testSession);
        sessionJpaRepository.saveAndFlush(testSession);

        testToken = generateTestToken(testUser);
        conferenceId = testConference.getId();
        sessionId = testSession.getId();
    }

    @Test
    @DisplayName("QR 코드 생성 성공")
    void QRCode_Create_Success() throws Exception {
        this.mockMvc.perform(get("/api/v1/admin/qr")
                        .cookie(new Cookie("Authorization", testToken))
                        .param("conferenceId", String.valueOf(conferenceId))
                        .param("sessionId", String.valueOf(sessionId))
                        .param("url", "https://www.google.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("QR 코드 생성 실패 - ConferenceId NOT FOUND")
    void QRCode_ConferenceId_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/admin/qr")
                        .cookie(new Cookie("Authorization", testToken))
                        .param("conferenceId", "999")
                        .param("sessionId", String.valueOf(sessionId))
                        .param("url", "https://www.google.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()),
                        jsonPath("$.message").value("존재하지 않는 컨퍼런스입니다.")
                );
    }

    @Test
    @DisplayName("QR 코드 생성 실패 - SessionId NOT FOUND")
    void QRCode_SessionId_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/admin/qr")
                        .cookie(new Cookie("Authorization", testToken))
                        .param("conferenceId", String.valueOf(conferenceId))
                        .param("sessionId", "999")
                        .param("url", "https://www.google.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()),
                        jsonPath("$.message").value("존재하지 않는 세션입니다.")
                );
    }

    private String generateTestToken(User user) {
        return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }
}
