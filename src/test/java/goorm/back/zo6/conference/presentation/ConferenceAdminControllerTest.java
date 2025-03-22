package goorm.back.zo6.conference.presentation;

import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.infrastructure.ConferenceJpaRepository;
import goorm.back.zo6.conference.infrastructure.SessionJpaRepository;
import goorm.back.zo6.config.RestDocsConfiguration;
import goorm.back.zo6.fixture.AdminFixture;
import goorm.back.zo6.fixture.ConferenceFixture;
import goorm.back.zo6.fixture.SessionFixture;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.infrastructure.UserJpaRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(RestDocsConfiguration.class)
public class ConferenceAdminControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RestDocumentationResultHandler restDocs;

    @Autowired
    private ConferenceJpaRepository conferenceRepository;

    @Autowired
    private SessionJpaRepository sessionJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private MockMvc mockMvc;

    private String testToken;

    private Conference conference;

    private Session session;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(restDocs)
                .build();

        User testUser = userJpaRepository.save(AdminFixture.관리자());
        this.testToken = generateTestToken(testUser);

        this.conference = conferenceRepository.save(ConferenceFixture.컨퍼런스());

        this.session = SessionFixture.세션(conference);
        this.conference.addSession(session);
        this.session = sessionJpaRepository.save(session);
    }

    @Test
    @DisplayName("관리자 - 모든 컨퍼런스 리스트 조회 성공")
    void allConferences_get_success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/conference")
                        .cookie(new Cookie("Authorization", testToken)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("관리자 - 특정 컨퍼런스 조회 성공")
    void conference_get_success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/conference/{conferenceId}", conference.getId())
                .cookie(new Cookie("Authorization", testToken)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("관리자 - 특정 컨퍼런스 내 특정 세션 조회 성공")
    void sessionDetail_get_success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/conference/{conferenceId}/sessions/{sessionId}", conference.getId(), session.getId())
                .cookie(new Cookie("Authorization", testToken)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("관리자 - 세션 상태 변경")
    void sessionStatus_put_success() throws Exception {
        mockMvc.perform(put("/api/v1/admin/conference/{conferenceId}/sessions/{sessionId}", conference.getId(), session.getId())
                .cookie(new Cookie("Authorization", testToken)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("관리자 - 세션 정보 수정")
    void sessionData_put_success() throws Exception {
        String requestBody = sessionRequestSetting();

        mockMvc.perform(put("/api/v1/admin/conference/{sessionId}", session.getId())
                        .contentType("application/json")
                        .content(requestBody)
                        .cookie(new Cookie("Authorization", testToken)))
                .andExpect(status().isOk());
    }

    private static String sessionRequestSetting() {
        String newLocation = "변경된 장소";
        return """
        {
            "location": "%s"
        }
        """.formatted(newLocation);
    }

    private String generateTestToken(User user) {
        return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }
}
