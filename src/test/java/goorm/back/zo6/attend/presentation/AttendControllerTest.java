package goorm.back.zo6.attend.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import goorm.back.zo6.attend.application.AttendService;
import goorm.back.zo6.attend.domain.Attend;
import goorm.back.zo6.attend.infrastructure.AttendJpaRepository;
import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.infrastructure.ConferenceJpaRepository;
import goorm.back.zo6.conference.infrastructure.SessionJpaRepository;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationSession;
import goorm.back.zo6.reservation.domain.ReservationStatus;
import goorm.back.zo6.reservation.infrastructure.ReservationJpaRepository;
import goorm.back.zo6.user.domain.Password;
import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import goorm.back.zo6.user.infrastructure.UserJpaRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AttendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttendService attendService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ConferenceJpaRepository conferenceJpaRepository;

    @Autowired
    private SessionJpaRepository sessionJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Autowired
    private AttendJpaRepository attendJpaRepository;

    private User testUser;
    private Conference testConferenceA;
    private Conference testConferenceB;
    private Conference testConferenceC;
    private Session testSession;
    private Reservation testReservationA;
    private Reservation testReservationB;
    private Reservation testReservationC;

    private Attend testAttendA;
    private Attend testAttendB;
    private String accessToken;

    @BeforeEach
    void setUp() {
        testUser = User.builder().name("홍길순").email("test@gmail.com").phone("010-1111-2222").password(Password.from(passwordEncoder.encode("1234"))).role(Role.of("USER")).build();
        userJpaRepository.saveAndFlush(testUser);

        testConferenceA = Conference.builder().name("컨퍼런스A").capacity(10).conferenceAt(LocalDateTime.now()).description("좋은 컨퍼런스A").hasSessions(true).location("서울 법성포A").imageKey("test.png").isActive(true).build();
        testConferenceB = Conference.builder().name("컨퍼런스B").capacity(10).conferenceAt(LocalDateTime.now()).description("좋은 컨퍼런스B").hasSessions(false).location("서울 법성포B").imageKey("test.png").isActive(true).build();
        testConferenceC = Conference.builder().name("컨퍼런스C").capacity(10).conferenceAt(LocalDateTime.now()).description("좋은 컨퍼런스C").hasSessions(false).location("서울 법성포C").imageKey("test.png").isActive(true).build();
        conferenceJpaRepository.saveAllAndFlush(List.of(testConferenceA, testConferenceB, testConferenceC));

        testSession = Session.builder().name("세션").capacity(10).summary("좋은 세션").conference(testConferenceA).location("서울 법성포 떡잎마을").time(LocalDateTime.now()).speakerName("발표자").speakerOrganization("발표자 소속").isActive(true).build();
        sessionJpaRepository.saveAndFlush(testSession);

        testReservationA = Reservation.builder().name("컨퍼런스 예매").status(ReservationStatus.CONFIRMED).phone("010-1111-2222").user(testUser).conference(testConferenceA).build();
        testReservationA.addSession(testSession);
        testReservationB = Reservation.builder().name("컨퍼런스 예매").status(ReservationStatus.CONFIRMED).phone("010-1111-2222").user(testUser).conference(testConferenceB).build();
        testReservationC = Reservation.builder().name("컨퍼런스 예매").status(ReservationStatus.CONFIRMED).phone("010-1111-2222").user(testUser).conference(testConferenceC).build();
        reservationJpaRepository.saveAllAndFlush(List.of(testReservationA, testReservationB,testReservationC));

        Optional<ReservationSession> testReservationSession = testReservationA.getReservationSessions().stream().findFirst();

        testAttendA = Attend.builder().userId(testUser.getId()).reservationId(testConferenceA.getId()).reservationSessionId(testReservationSession.get().getId()).conferenceId(testConferenceA.getId()).sessionId(testSession.getId()).build();
        testAttendB = Attend.builder().userId(testUser.getId()).reservationId(testConferenceB.getId()).reservationSessionId(null).conferenceId(testConferenceB.getId()).sessionId(null).build();
        attendJpaRepository.saveAllAndFlush(List.of(testAttendA, testAttendB));

        accessToken = generateTestToken(testUser);
    }

    private String generateTestToken(User user) {
        return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }

    @Test
    @DisplayName("토큰 기반 유저의 행사 참가 내역을 조회 - 컨퍼런스 세션 존재 및 컨퍼런스 세션 참석 성공")
    void findByToken_ConferenceSessionSuccess() throws Exception {
        // given
        // when & then
        mockMvc.perform(get("/api/v1/attend")
                        .cookie(new Cookie("Authorization", accessToken))
                        .param("conferenceId", String.valueOf(testConferenceA.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testConferenceA.getId()))
                .andExpect(jsonPath("$.name").value("컨퍼런스A")) // 컨퍼런스 이름 검증
                .andExpect(jsonPath("$.location").value("서울 법성포A")) // 위치 검증
                .andExpect(jsonPath("$.capacity").value(10)) // 정원 검증
                .andExpect(jsonPath("$.description").value("좋은 컨퍼런스A")) // 설명 검증
                .andExpect(jsonPath("$.hasSessions").value(true)) // 세션 존재 여부 검증
                .andExpect(jsonPath("$.sessions[0].id").value(testSession.getId())) // 첫 번째 세션 ID 검증
                .andExpect(jsonPath("$.sessions[0].name").value("세션")) // 첫 번째 세션 이름 검증
                .andExpect(jsonPath("$.sessions[0].location").value("서울 법성포 떡잎마을")) // 첫 번째 세션 위치 검증
                .andExpect(jsonPath("$.sessions[0].summary").value("좋은 세션")) // 첫 번째 세션 설명 검증
                .andExpect(jsonPath("$.sessions[0].attend").value(true)) // 첫 번째 세션 참석 여부 검증
                .andExpect(jsonPath("$.attend").value(true)) // 컨퍼런스 참석 여부 검증
                .andDo(print());
    }

    @Test
    @DisplayName("토큰 기반 유저의 행사 참가 내역을 조회 - 컨퍼런스 존재 및 컨퍼런스 참석 성공")
    void findByToken_ConferenceSuccess() throws Exception {
        // given
        // when & then
        mockMvc.perform(get("/api/v1/attend")
                        .cookie(new Cookie("Authorization", accessToken))
                        .param("conferenceId", String.valueOf(testConferenceB.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testConferenceB.getId()))
                .andExpect(jsonPath("$.name").value("컨퍼런스B")) // 컨퍼런스 이름 검증
                .andExpect(jsonPath("$.location").value("서울 법성포B")) // 위치 검증
                .andExpect(jsonPath("$.capacity").value(10)) // 정원 검증
                .andExpect(jsonPath("$.description").value("좋은 컨퍼런스B")) // 설명 검증
                .andExpect(jsonPath("$.hasSessions").value(false)) // 세션 존재 여부 검증
                .andExpect(jsonPath("$.attend").value(true)) // 컨퍼런스 참석 여부 검증
                .andDo(print());
    }

    @Test
    @DisplayName("토큰 기반 유저의 행사 참가 내역을 조회 - 컨퍼런스 존재 및 컨퍼런스 참석 실패")
    void findByToken_ConferenceFails() throws Exception {
        // given
        // when & then
        mockMvc.perform(get("/api/v1/attend")
                        .cookie(new Cookie("Authorization", accessToken))
                        .param("conferenceId", String.valueOf(testConferenceC.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testConferenceC.getId()))
                .andExpect(jsonPath("$.name").value("컨퍼런스C")) // 컨퍼런스 이름 검증
                .andExpect(jsonPath("$.location").value("서울 법성포C")) // 위치 검증
                .andExpect(jsonPath("$.capacity").value(10)) // 정원 검증
                .andExpect(jsonPath("$.description").value("좋은 컨퍼런스C")) // 설명 검증
                .andExpect(jsonPath("$.hasSessions").value(false)) // 세션 존재 여부 검증
                .andExpect(jsonPath("$.attend").value(false)) // 컨퍼런스 참석 여부 검증
                .andDo(print());
    }
}