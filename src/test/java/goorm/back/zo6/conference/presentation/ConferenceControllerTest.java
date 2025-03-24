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
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(restDocs)
                .build();

        User testUser = userJpaRepository.save(UserFixture.유저());
        testToken = generateTestToken(testUser);
    }

    @Test
    @DisplayName("모든 컨퍼런스 리스트 조회 성공")
    void getAllConferences_ReturnsConferenceList() throws Exception {
        Conference conference = conferenceJpaRepository.save(ConferenceFixture.컨퍼런스());

        mockMvc.perform(get("/api/v1/conference")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].name").value(conference.getName()))
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("status").description(true),
                                fieldWithPath(".data.[].id").type(NUMBER).description("컨퍼런스 ID"),
                                fieldWithPath(".data.[].name").type(STRING).description("컨퍼런스 이름"),
                                fieldWithPath(".data.[].description").type(STRING).description("컨퍼런스 설명"),
                                fieldWithPath(".data.[].location").type(STRING).description("컨퍼런스 장소"),
                                fieldWithPath(".data.[].startTime").type(STRING).description("컨퍼런스 시작 일정"),
                                fieldWithPath(".data.[].endTime").type(STRING).description("컨퍼런스 종료 일정"),
                                fieldWithPath(".data.[].capacity").type(NUMBER).description("컨퍼런스 수용인원"),
                                fieldWithPath(".data.[].imageUrl").type(STRING).description("컨퍼런스 이미지"),
                                fieldWithPath(".data.[].isActive").type(BOOLEAN).description("활성화"),
                                fieldWithPath(".data.[].hasSessions").type(BOOLEAN).description("세션 존재 여부")
                        )
                ));
    }

    @Test
    @DisplayName("특정 컨퍼런스 조회 성공")
    void getConference_ReturnsSpecificConference() throws Exception {
        Conference conference = conferenceJpaRepository.save(ConferenceFixture.컨퍼런스());

        mockMvc.perform(get("/api/v1/conference/{conferenceId}", conference.getId())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(conference.getId()))
                .andExpect(jsonPath("$.data.name").value(conference.getName()))
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("status").description(true),
                                fieldWithPath(".data.id").type(NUMBER).description("컨퍼런스 ID"),
                                fieldWithPath(".data.name").type(STRING).description("컨퍼런스 이름"),
                                fieldWithPath(".data.description").type(STRING).description("컨퍼런스 설명"),
                                fieldWithPath(".data.location").type(STRING).description("컨퍼런스 장소"),
                                fieldWithPath(".data.startTime").type(STRING).description("컨퍼런스 시작 일정"),
                                fieldWithPath(".data.endTime").type(STRING).description("컨퍼런스 종료 일정"),
                                fieldWithPath(".data.capacity").type(NUMBER).description("컨퍼런스 수용인원"),
                                fieldWithPath(".data.hasSessions").type(BOOLEAN).description("세션 존재 여부"),
                                fieldWithPath(".data.imageUrl").type(STRING).description("컨퍼런스 이미지"),
                                fieldWithPath(".data.isActive").type(BOOLEAN).description("활성화"),
                                fieldWithPath(".data.sessions[]").type(ARRAY).description("세션 목록")
                        )
                ));
    }

    @Test
    @DisplayName("특정 컨퍼런스 내 특정 세션 조회 성공")
    void getSessionDetail_ReturnsSpecificSession() throws Exception {
        Conference savedConference = conferenceJpaRepository.save(ConferenceFixture.컨퍼런스());

        Session session = SessionFixture.세션(savedConference);

        savedConference.addSession(session);
        Session savedSession = sessionJpaRepository.save(session);


        mockMvc.perform(get("/api/v1/conference/{conferenceId}/sessions/{sessionId}", savedConference.getId(), session.getId())
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(savedSession.getId()))
                .andExpect(jsonPath("$.data.conferenceId").value(savedConference.getId()))
                .andExpect(jsonPath("$.data.name").value(savedSession.getName()))
                .andExpect(jsonPath("$.data.capacity").value(savedSession.getCapacity()))
                .andExpect(jsonPath("$.data.location").value(savedSession.getLocation()))
                .andExpect(jsonPath("$.data.summary").value(savedSession.getSummary()))
                .andExpect(jsonPath("$.data.speakerName").value(savedSession.getSpeakerName()))
                .andExpect(jsonPath("$.data.speakerOrganization").value(savedSession.getSpeakerOrganization()))
                .andExpect(jsonPath("$.data.active").value(savedSession.isActive()))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("conferenceId").description("조회할 컨퍼런스 ID"),
                                parameterWithName("sessionId").description("조회할 세션 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description(true),
                                fieldWithPath(".data.id").description("세션 ID"),
                                fieldWithPath(".data.conferenceId").description("컨퍼런스 ID"),
                                fieldWithPath(".data.name").description("세션 이름"),
                                fieldWithPath(".data.capacity").description("세션 수용 가능 인원"),
                                fieldWithPath(".data.location").description("세션 장소"),
                                fieldWithPath(".data.startTime").description("세션 시작 일정"),
                                fieldWithPath(".data.endTime").description("세션 종료 일정"),
                                fieldWithPath(".data.summary").description("세션 요약"),
                                fieldWithPath(".data.speakerName").description("발표자"),
                                fieldWithPath(".data.speakerOrganization").description("발표자 소속"),
                                fieldWithPath(".data.active").description("활성화"),
                                fieldWithPath(".data.speakerImage").description("테스트 이미지")
                        )
                ));
    }

    private String generateTestToken(User user) {
        return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole());
    }
}
