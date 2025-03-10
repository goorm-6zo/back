package goorm.back.zo6.conference.presentation;

import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.infrastructure.ConferenceJpaRepository;
import goorm.back.zo6.config.RestDocsConfiguration;
import goorm.back.zo6.fixture.ConferenceFixture;
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
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(RestDocumentationExtension.class)
@Import(RestDocsConfiguration.class)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ConferenceControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private RestDocumentationResultHandler restDocs;

    @Autowired
    private ConferenceJpaRepository conferenceJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String testToken;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {

        conferenceJpaRepository.deleteAll();

        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(restDocs)
                .build();

        User testUser = userJpaRepository.saveAndFlush(UserFixture.유저());
        Conference testConference = conferenceJpaRepository.saveAndFlush(ConferenceFixture.컨퍼런스());

        testToken = generateTestToken(testUser);
    }

    @Test
    @DisplayName("모든 컨퍼런스 리스트 조회 성공")
    void getALLConferences_ReturnsConferenceList() throws Exception {
        mockMvc.perform(get("/api/v1/conference")
                .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("존재하지 않는 컨퍼런스 요청 실패")
    void getConference_NotFound() throws Exception {

        Long invalidConferenceId = 999L;

        mockMvc.perform(get("/api/v1/conference/" + invalidConferenceId)
                .header("Authorization", "Bearer " + testToken))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value("Conference not found."));
    }

    @Test
    @DisplayName("특정 컨퍼런스 조회")
    void getConference_ReturnsSpecificConference() throws Exception {
        Conference testConference = conferenceJpaRepository.findAll().get(0);
        Long testConferenceId = testConference.getId();

        mockMvc.perform(get("/api/v1/conference/" + testConferenceId)
                .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testConference.getId()))
                .andExpect(jsonPath("$.name").value(testConference.getName()));
    }

    private String generateTestToken(User user) {
        return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }
}
