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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.context.annotation.Import;

import java.util.Collections;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
                .alwaysDo(restDocs) // ← 팀원 설정 restDocs를 적용
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
                .andExpect(jsonPath("$[0].name").value(conference.getName()))
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("컨퍼런스 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("컨퍼런스 이름"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("컨퍼런스 설명"),
                                fieldWithPath("[].location").type(JsonFieldType.STRING).description("컨퍼런스 장소"),
                                fieldWithPath("[].conferenceAt").type(JsonFieldType.STRING).description("컨퍼런스 일정"),
                                fieldWithPath("[].capacity").type(JsonFieldType.NUMBER).description("컨퍼런스 수용인원"),
                                fieldWithPath("[].hasSessions").type(JsonFieldType.BOOLEAN).description("세션 존재 여부")
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
                .andExpect(jsonPath("$.id").value(conference.getId()))
                .andExpect(jsonPath("$.name").value(conference.getName()))
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("컨퍼런스 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("컨퍼런스 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("컨퍼런스 설명"),
                                fieldWithPath("location").type(JsonFieldType.STRING).description("컨퍼런스 장소"),
                                fieldWithPath("conferenceAt").type(JsonFieldType.STRING).description("컨퍼런스 일정"),
                                fieldWithPath("capacity").type(JsonFieldType.NUMBER).description("컨퍼런스 수용인원"),
                                fieldWithPath("hasSessions").type(JsonFieldType.BOOLEAN).description("세션 존재 여부"),
                                fieldWithPath("sessions[]").type(JsonFieldType.ARRAY).description("세션 목록")
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
                .andExpect(jsonPath("$.id").value(savedSession.getId()))
                .andExpect(jsonPath("$.conferenceId").value(savedConference.getId()))
                .andExpect(jsonPath("$.name").value(savedSession.getName()))
                .andExpect(jsonPath("$.capacity").value(savedSession.getCapacity()))
                .andExpect(jsonPath("$.location").value(savedSession.getLocation()))
                .andExpect(jsonPath("$.summary").value(savedSession.getSummary()))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("conferenceId").description("조회할 컨퍼런스 ID"),
                                parameterWithName("sessionId").description("조회할 세션 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("세션 ID"),
                                fieldWithPath("conferenceId").description("컨퍼런스 ID"),
                                fieldWithPath("name").description("세션 이름"),
                                fieldWithPath("capacity").description("세션 수용 가능 인원"),
                                fieldWithPath("location").description("세션 장소"),
                                fieldWithPath("time").description("세션 일정"),
                                fieldWithPath("summary").description("세션 요약")
                        )
                ));

    }

    private String generateTestToken(User user) {
        return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }
}
