package goorm.back.zo6.conference.application;

import goorm.back.zo6.conference.application.dto.ConferenceResponse;
import goorm.back.zo6.conference.application.dto.SessionDto;
import goorm.back.zo6.conference.application.query.ConferenceQueryServiceImpl;
import goorm.back.zo6.conference.application.shared.ConferenceMapper;
import goorm.back.zo6.conference.application.shared.ConferenceValidator;
import goorm.back.zo6.conference.application.shared.SessionValidator;
import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.ConferenceRepository;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.conference.domain.SessionRepository;
import goorm.back.zo6.fixture.ConferenceFixture;
import goorm.back.zo6.fixture.SessionFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConferenceQueryServiceImplTest {

    @Mock
    private ConferenceRepository conferenceRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private SessionValidator sessionValidator;

    @Mock
    private ConferenceValidator conferenceValidator;

    @Mock
    private ConferenceMapper conferenceMapper;

    @InjectMocks
    private ConferenceQueryServiceImpl conferenceQueryService;

    @Test
    @DisplayName("모든 컨퍼런스를 성공적으로 조회한다")
    void getAllConferences_Success() {
        Conference conference = ConferenceFixture.컨퍼런스_아이디포함();
        ConferenceResponse response = ConferenceResponse.from(conference, conference.getImageKey());

        List<Conference> conferences = List.of(conference);

        when(conferenceRepository.findAll()).thenReturn(conferences);
        when(conferenceMapper.toConferenceResponse(conference)).thenReturn(response);

        List<ConferenceResponse> result = conferenceQueryService.getAllConferences();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));

        verify(conferenceRepository).findAll();
        verify(conferenceMapper).toConferenceResponse(conference);
    }

    @Test
    @DisplayName("특정 컨퍼런스를 성공적으로 조회한다")
    void getConference_Success() {
        Conference conference = ConferenceFixture.컨퍼런스_아이디포함();
        Session session = SessionFixture.세션_아이디포함(conference);
        conference.getSessions().add(session);

        ConferenceResponse detailResponse = ConferenceResponse.detailFrom(conference, conference.getImageKey(), List.of(SessionDto.fromEntity(session)));

        when(conferenceValidator.findConferenceWithSessionsOrThrow(conference.getId())).thenReturn(conference);
        when(conferenceMapper.toConferenceDetailResponse(conference)).thenReturn(detailResponse);

        ConferenceResponse result = conferenceQueryService.getConference(conference.getId());

        assertNotNull(result);
        assertEquals(conference.getId(), result.id());
        verify(conferenceValidator).findConferenceWithSessionsOrThrow(conference.getId());
        verify(conferenceMapper).toConferenceDetailResponse(conference);
    }

    @Test
    @DisplayName("컨퍼런스의 세션 목록 조회를 성공한다")
    void getSessionsByConferenceId_Success() {
        Conference conference = ConferenceFixture.컨퍼런스_아이디포함();
        Session session = SessionFixture.세션_아이디포함(conference);
        conference.getSessions().add(session);

        SessionDto sessionDto = SessionDto.fromEntity(session);

        when(conferenceValidator.findConferenceWithSessionsOrThrow(conference.getId())).thenReturn(conference);
        when(conferenceMapper.toSessionDto(session)).thenReturn(sessionDto);

        List<SessionDto> responses = conferenceQueryService.getSessionsByConferenceId(conference.getId());

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(conferenceValidator).findConferenceWithSessionsOrThrow(conference.getId());
        verify(conferenceMapper).toSessionDto(session);
    }

    @Test
    @DisplayName("세션 ID로 세션 단건 조회를 성공한다")
    void getSessionById_Success() {

        Long conferenceId = 1L;
        Long sessionId = 1L;

        Conference conference = ConferenceFixture.컨퍼런스_아이디포함();
        Session session = SessionFixture.세션_아이디포함(conference);
        conference.getSessions().add(session);

        SessionDto sessionDto = SessionDto.fromEntity(session);

        when(conferenceValidator.findConferenceWithSessionsOrThrow(conferenceId)).thenReturn(conference);
        when(sessionValidator.getSessionOrThrow(sessionId)).thenReturn(session);
        when(conferenceMapper.toSessionDto(session)).thenReturn(sessionDto);

        SessionDto result = conferenceQueryService.getSessionDetail(conferenceId,sessionId);

        assertNotNull(result);
        assertEquals(sessionDto, result);

        verify(conferenceValidator).findConferenceWithSessionsOrThrow(conferenceId);
        verify(sessionValidator).getSessionOrThrow(sessionId);
        verify(conferenceMapper).toSessionDto(session);
    }

    @Test
    @DisplayName("특정 컨퍼런스 내 특정 세션 상세 조회를 성공한다")
    void getSessionDetail_Success() {

        Long conferenceId = 1L;
        Long sessionId = 1L;

        Conference conference = ConferenceFixture.컨퍼런스_아이디포함();
        Session session = SessionFixture.세션_아이디포함(conference);
        conference.getSessions().add(session);

        SessionDto sessionDto = SessionDto.fromEntity(session);

        when(conferenceValidator.findConferenceWithSessionsOrThrow(conferenceId)).thenReturn(conference);
        when(sessionValidator.getSessionOrThrow(sessionId)).thenReturn(session);
        when(conferenceMapper.toSessionDto(session)).thenReturn(sessionDto);

        SessionDto result = conferenceQueryService.getSessionDetail(conferenceId,sessionId);

        assertNotNull(result);
        assertEquals(sessionDto, result);

        verify(conferenceValidator).findConferenceWithSessionsOrThrow(conferenceId);
        verify(sessionValidator).getSessionOrThrow(sessionId);
        verify(conferenceMapper).toSessionDto(session);
    }
}