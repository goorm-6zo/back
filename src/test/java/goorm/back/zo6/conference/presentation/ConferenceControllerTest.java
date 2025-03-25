package goorm.back.zo6.conference.presentation;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(RestDocsConfiguration.class)
class ConferenceControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RestDocumentationResultHandler restDocs;

    @Autowired
    private ConferenceJpaRepository conferenceJpaRepository;

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

        sessionJpaRepository.deleteAll();
        conferenceJpaRepository.deleteAll();

        User testUser = userJpaRepository.save(UserFixture.유저());
        testToken = generateTestToken(testUser);

        this.conference = conferenceJpaRepository.save(ConferenceFixture.컨퍼런스());

        this.session = SessionFixture.세션(conference);
        this.conference.addSession(session);
        this.sessionJpaRepository.save(session);
    }

    @Test
    @DisplayName("모든 컨퍼런스 리스트 조회 성공")
    void getAllConferences_ReturnsConferenceList() throws Exception {
        mockMvc.perform(get("/api/v1/conference")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].name").value(conference.getName()));
    }

    @Test
    @DisplayName("특정 컨퍼런스 조회 성공")
    void getConference_ReturnsSpecificConference() throws Exception {
        mockMvc.perform(get("/api/v1/conference/{conferenceId}", conference.getId())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(conference.getId()))
                .andExpect(jsonPath("$.data.name").value(conference.getName()));
    }

    @Test
    @DisplayName("특정 컨퍼런스 내 특정 세션 조회 성공")
    void getSessionDetail_ReturnsSpecificSession() throws Exception {
        mockMvc.perform(get("/api/v1/conference/{conferenceId}/sessions/{sessionId}", conference.getId(), session.getId())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(session.getId()))
                .andExpect(jsonPath("$.data.conferenceId").value(session.getId()))
                .andExpect(jsonPath("$.data.name").value(session.getName()))
                .andExpect(jsonPath("$.data.capacity").value(session.getCapacity()))
                .andExpect(jsonPath("$.data.location").value(session.getLocation()))
                .andExpect(jsonPath("$.data.summary").value(session.getSummary()))
                .andExpect(jsonPath("$.data.speakerName").value(session.getSpeakerName()))
                .andExpect(jsonPath("$.data.speakerOrganization").value(session.getSpeakerOrganization()))
                .andExpect(jsonPath("$.data.active").value(session.isActive()));
    }

    private String generateTestToken(User user) {
        return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole());
    }
}
