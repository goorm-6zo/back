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

import java.time.LocalDateTime;
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
    private Conference testConferenceId;

    @BeforeEach
    void setUp() {
        testConference = ConferenceFixture.컨퍼런스();
        testConferenceId = ConferenceFixture.컨퍼런스_아이디포함();
        testSession = SessionFixture.세션_아이디포함(testConferenceId);
        testConference.getSessions().add(testSession);
        testConferenceId.getSessions().add(testSession);
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
                            conference.getDescription(),
                            conference.getLocation(),
                            conference.getConferenceAt(),
                            conference.getCapacity(),
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
                            conference.getDescription(),
                            conference.getLocation(),
                            conference.getConferenceAt(),
                            conference.getCapacity(),
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
    @DisplayName("Session -> SessionDto 매핑 테스트")
    void toSessionDto_ShouldMapSessionToDtoProperly() {
        // Given
        Session session = new Session(
                100L,
                testConferenceId,
                "테스트 컨퍼런스",
                100,
                "온라인",
                LocalDateTime.of(2025, 3, 13, 11, 51, 21),
                "컨퍼런스 내용"
        );

        when(conferenceMapper.toSessionDto(any(Session.class)))
                .thenAnswer(invocation -> {
                    Session sessionArg = invocation.getArgument(0);
                    return new SessionDto(
                            sessionArg.getId(),
                            sessionArg.getConference().getId() == null ? null : sessionArg.getConference().getId(),
                            sessionArg.getName(),
                            sessionArg.getCapacity(),
                            sessionArg.getLocation(),
                            sessionArg.getTime(),
                            sessionArg.getSummary()
                    );
                });

        // When
        SessionDto sessionDto = conferenceMapper.toSessionDto(session);

        // Then
        assertThat(sessionDto).isNotNull();
        assertThat(sessionDto.getId()).isEqualTo(session.getId());
        assertThat(sessionDto.getName()).isEqualTo(session.getName());
        assertThat(sessionDto.getLocation()).isEqualTo(session.getLocation());
        assertThat(sessionDto.getTime()).isEqualTo(session.getTime());
        assertThat(sessionDto.getSummary()).isEqualTo(session.getSummary());
    }

    @Test
    @DisplayName("컨퍼런스의 세션 목록 조회 성공")
    void getSessionsByConferenceId_WhenValidConference_ReturnsSessions() {
        // Given
        Long conferenceId = 1L;

        // Mock Conference 및 Session 설정
        testConferenceId.getSessions().add(testSession); // 세션 추가
        when(conferenceRepository.findWithSessionsById(conferenceId))
                .thenReturn(Optional.of(testConferenceId));

        // Mock Mapper 설정
        when(conferenceMapper.toSessionDto(any(Session.class)))
                .thenReturn(new SessionDto(
                        testSession.getId(),
                        testConferenceId.getId(),
                        testSession.getName(),
                        testSession.getCapacity(),
                        testSession.getLocation(),
                        testSession.getTime(),
                        testSession.getSummary()
                ));

        // When
        List<SessionDto> sessionDtos = conferenceQueryServiceImpl.getSessionsByConferenceIdDto(conferenceId);

        // Then
        assertThat(sessionDtos).hasSize(1); // ensure a single session is returned
        assertThat(sessionDtos.get(0).getId()).isEqualTo(testSession.getId());
        assertThat(sessionDtos.get(0).getName()).isEqualTo(testSession.getName());

        // Mock 동작 검증
        verify(conferenceRepository, times(1)).findWithSessionsById(conferenceId);
        verify(conferenceMapper, times(1)).toSessionDto(testSession);
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
    @DisplayName("특정 컨퍼런스의 특정 세션 상세 조회 - 성공")
    void getSessionDetail_WhenSessionBelongsToConference_ReturnsSession() {
        Long conferenceId = 1L;
        Long sessionId = 100L;

        when(conferenceRepository.findWithSessionsById(conferenceId)).thenReturn(Optional.of(testConference));
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(testSession));

        Session result = conferenceQueryServiceImpl.getSessionDetail(conferenceId, sessionId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(sessionId);
        verify(conferenceRepository, times(1)).findWithSessionsById(conferenceId);
        verify(sessionRepository, times(1)).findById(sessionId);
    }
}