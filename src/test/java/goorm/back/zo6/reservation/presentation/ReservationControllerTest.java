package goorm.back.zo6.reservation.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.infrastructure.ConferenceJpaRepository;
import goorm.back.zo6.conference.infrastructure.SessionJpaRepository;
import goorm.back.zo6.config.RestDocsConfiguration;
import goorm.back.zo6.fixture.ConferenceFixture;
import goorm.back.zo6.fixture.ReservationFixture;
import goorm.back.zo6.fixture.SessionFixture;
import goorm.back.zo6.fixture.UserFixture;
import goorm.back.zo6.reservation.application.ReservationRequest;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.infrastructure.ReservationJpaRepository;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.infrastructure.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(RestDocumentationExtension.class)
@Import(RestDocsConfiguration.class)
@Transactional
class ReservationControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private RestDocumentationResultHandler restDocs;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String testToken;

    private User testUser;

    private Conference testConference;

    private Reservation testReservation;

    @Autowired
    private ConferenceJpaRepository conferenceJpaRepository;

    @Autowired
    private SessionJpaRepository sessionJpaRepository;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(restDocs)
                .build();

        testUser = userJpaRepository.save(UserFixture.유저());
        testToken = generateTestToken(testUser);

        testConference = conferenceJpaRepository.save(ConferenceFixture.컨퍼런스());
        Session session = sessionJpaRepository.save(SessionFixture.세션(testConference));

        testReservation = reservationJpaRepository.save(ReservationFixture.확정된예약(
                testConference,
                List.of(session),
                testUser.getName(),
                testUser.getPhone(),
                testUser
                )
        );

        System.out.println("testReservation : " + testReservation.getId());
        System.out.println("testUser : " + testUser.getId());
        System.out.println(testConference.getId());

        Authentication auth = new UsernamePasswordAuthenticationToken(
                testUser.getEmail(), null, List.of(new SimpleGrantedAuthority(testUser.getRole().name()))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void 예약_임시생성_API테스트() throws Exception {
        Conference conference = conferenceJpaRepository.save(ConferenceFixture.컨퍼런스());
        Session session = sessionJpaRepository.save(SessionFixture.세션(conference));

        conference.addSession(session);

        ReservationRequest request = ReservationRequest.builder()
                .conferenceId(conference.getId())
                .sessionIds(List.of(session.getId()))
                .name(testUser.getName())
                .phone(testUser.getPhone())
                .build();

        mockMvc.perform(post("/api/v1/reservation/temp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(restDocs);
    }

    @Test
    void 내_예약_모두조회_API_테스트() throws Exception {
        mockMvc.perform(get("/api/v1/reservation/my")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs);
    }

    @Test
    void 내가_예약한_컨퍼런스목록조회_API_테스트() throws Exception {
        mockMvc.perform(get("/api/v1/reservation/my/conference")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs);
    }

    @Test
    void 예약한_특정_컨퍼런스상세조회_API_테스트() throws Exception {
        mockMvc.perform(get("/api/v1/reservation/my/conference/{conferenceId}", testConference.getId())
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs);
    }

    @Test
    void 예약과_사용자연결_API_테스트() throws Exception {

        Conference conference = conferenceJpaRepository.save(ConferenceFixture.컨퍼런스());
        Session session = sessionJpaRepository.save(SessionFixture.세션(conference));

        System.out.println("conferenceId : " + conference.getId());
        System.out.println("sessionId : " + session.getId());

        conference.addSession(session);

        ReservationRequest tempReservationRequest = ReservationRequest.builder()
                .conferenceId(conference.getId())
                .sessionIds(List.of(session.getId()))
                .name(testUser.getName())
                .phone(testUser.getPhone())
                .build();

        mockMvc.perform(post("/api/v1/reservation/temp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tempReservationRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/reservation/link-user")
                        .header("Authorization", "Bearer " + testToken)
                        .param("phone", testUser.getPhone())
                        .param("userId", testUser.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs);
    }

    private String generateTestToken(User user) {
        return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }
}
