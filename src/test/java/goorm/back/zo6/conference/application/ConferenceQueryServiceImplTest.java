package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.ConferenceRepository;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.domain.SessionRepository;
import goorm.back.zo6.fixture.ConferenceFixture;
import goorm.back.zo6.fixture.SessionFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConferenceQueryServiceImplTest {

    @Mock
    private ConferenceRepository conferenceRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private ConferenceMapper conferenceMapper;

    @InjectMocks
    private ConferenceQueryServiceImpl conferenceQueryServiceImpl;

    private Conference testConference;
    private Session testSession;

    @BeforeEach
    void setUp() {
        testConference = ConferenceFixture.컨퍼런스();
        testSession = SessionFixture.세션(testConference);
        testConference.getSessions().add(testSession);
    }

    @Test
    @DisplayName("모든 컨퍼런스 조회 성공")
    void getAllConferences_ReturnsConferenceResponses() {

        when(conferenceRepository.findAll()).thenReturn(List.of(testConference));
        when(conferenceMapper.toConferenceResponse(any(Conference.class)))
                .thenAnswer(invocation -> {
                    Conference conference = invocation.getArgument(0);
                    return new ConferenceResponse(
                            conference.getId(),
                            conference.getName(),
                            conference.getHasSessions()
                    );
                });

        List<ConferenceResponse> conferences = conferenceQueryServiceImpl.getAllConferences();

        assertThat(conferences).hasSize(1);
        assertThat(conferences.get(0)).isNotNull();
        assertThat(conferences.get(0).getName()).isEqualTo(testConference.getName());

        verify(conferenceRepository, times(1)).findAll();
        verify(conferenceMapper, times(1)).toConferenceResponse(any(Conference.class));
    }

    @Test
    @DisplayName("컨퍼런스 ID로 조회 성공")
    void getConference_WhenConferenceExists_ReturnsDetailResponse() {

        Long conferenceId = 1L;
        when(conferenceRepository.findWithSessionsById(conferenceId)).thenReturn(Optional.of(testConference));
        when(conferenceMapper.toConferenceDetailResponse(any(Conference.class)))
                .thenAnswer(invocation -> {
                    Conference conference = invocation.getArgument(0);
                    return new ConferenceDetailResponse(
                            conference.getId(),
                            conference.getName(),
                            conference.getHasSessions(),
                            List.of()
                    );
                });

        ConferenceDetailResponse response = conferenceQueryServiceImpl.getConference(conferenceId);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(testConference.getName());

        verify(conferenceRepository, times(1)).findWithSessionsById(conferenceId);
        verify(conferenceMapper, times(1)).toConferenceDetailResponse(any(Conference.class));
    }

    @Test
    @DisplayName("존재하지 않는 컨퍼런스 조회 시 예외 발생")
    void getConference_WhenNotFound_ThrowsException() {

        Long invalidConferenceId = 999L;
        when(conferenceRepository.findWithSessionsById(invalidConferenceId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> conferenceQueryServiceImpl.getConference(invalidConferenceId));

        verify(conferenceRepository, times(1)).findWithSessionsById(invalidConferenceId);
    }

    @Test
    @DisplayName("컨퍼런스의 세션 목록 조회 성공")
    void getSessionsByConferenceId_WhenValidConference_ReturnsSessions() {

        Long conferenceId = 1L;
        when(conferenceRepository.findWithSessionsById(conferenceId)).thenReturn(Optional.of(testConference));
        when(sessionRepository.findByConferenceId(conferenceId)).thenReturn(List.of(testSession));

        List<Session> sessions = conferenceQueryServiceImpl.getSessionsByConferenceId(conferenceId);

        assertThat(sessions).hasSize(1);
        assertThat(sessions.get(0).getName()).isEqualTo(testSession.getName());

        verify(sessionRepository, times(1)).findByConferenceId(conferenceId);
    }

    @Test
    @DisplayName("세션이 없는 컨퍼런스에서 세션 조회 시 예외 발생")
    void getSessionsByConferenceId_WhenNoSession_ThrowsException() {

        Long conferenceId = 1L;
        testConference = Conference.builder()
                .name("테스트 컨퍼런스")
                .hasSessions(false)
                .sessions(new HashSet<>())
                .build();
        when(conferenceRepository.findWithSessionsById(conferenceId)).thenReturn(Optional.of(testConference));

        assertThrows(IllegalArgumentException.class, () -> conferenceQueryServiceImpl.getSessionsByConferenceId(conferenceId));

        verify(conferenceRepository, times(1)).findWithSessionsById(conferenceId);
        verifyNoInteractions(sessionRepository);
    }

    @Test
    @DisplayName("세션 ID로 세션 정보 조회 성공")
    void getSessionById_WhenValidSession_ReturnsSession() {

        Long sessionId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));

        Session session = conferenceQueryServiceImpl.getSessionById(sessionId);

        assertThat(session).isNotNull();
        assertThat(session.getName()).isEqualTo(testSession.getName());

        verify(sessionRepository, times(1)).findById(sessionId);
    }

    @Test
    @DisplayName("세션 ID로 세션 조회 시 존재하지 않으면 예외 발생")
    void getSessionById_WhenNoSessionFound_ThrowsException() {

        Long invalidSessionId = 999L;
        when(sessionRepository.findById(invalidSessionId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> conferenceQueryServiceImpl.getSessionById(invalidSessionId));

        verify(sessionRepository, times(1)).findById(invalidSessionId);
    }

    @Test
    @DisplayName("세션 예약 가능 여부 조회 - 가능한 경우")
    void isSessionReservable_WhenReservable_ReturnsTrue() {

        Long sessionId = 1L;
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));

        boolean result = conferenceQueryServiceImpl.isSessionReservable(sessionId);

        assertThat(result).isTrue();

        verify(sessionRepository, times(1)).findById(sessionId);
    }

    @Test
    @DisplayName("세션 예약 가능 여부 조회 - 예약 불가능한 경우")
    void isSessionReservable_WhenNotReservable_ReturnsFalse() {

        Long sessionId = 1L;
        testSession = Session.builder()
                .conference(testConference)
                .name("Non-Reservable Session")
                .capacity(0)
                .location("Offline")
                .time(null)
                .summary("This session is not reservable.")
                .build();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));

        boolean result = conferenceQueryServiceImpl.isSessionReservable(sessionId);

        assertThat(result).isFalse();

        verify(sessionRepository, times(1)).findById(sessionId);
    }


}
