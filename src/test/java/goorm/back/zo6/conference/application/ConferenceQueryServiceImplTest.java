package goorm.back.zo6.conference.application;

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
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ConferenceQueryServiceImplTest {

    @Mock
    private ConferenceRepository conferenceRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private ConferenceMapper conferenceMapper;

    @InjectMocks
    private ConferenceQueryServiceImpl conferenceQueryService;

    @Test
    @DisplayName("모든 컨퍼런스를 성공적으로 조회한다")
    void getAllConferences_Success() {
        Conference conference = ConferenceFixture.컨퍼런스_아이디포함();
        ConferenceResponse response = new ConferenceResponse(
                conference.getId(),
                conference.getName(),
                conference.getDescription(),
                conference.getLocation(),
                conference.getConferenceAt(),
                conference.getCapacity(),
                conference.getHasSessions()
        );

        given(conferenceRepository.findAll()).willReturn(List.of(conference));
        given(conferenceMapper.toConferenceResponse(conference)).willReturn(response);

        List<ConferenceResponse> responses = conferenceQueryService.getAllConferences();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(conference.getId());
    }

    @Test
    @DisplayName("특정 컨퍼런스를 성공적으로 조회한다")
    void getConference_Success() {
        Conference conference = ConferenceFixture.컨퍼런스_아이디포함();
        Session session = SessionFixture.세션_아이디포함(conference);
        conference.getSessions().add(session);

        ConferenceDetailResponse detailResponse = new ConferenceDetailResponse(
                conference.getId(),
                conference.getName(),
                conference.getDescription(),
                conference.getLocation(),
                conference.getConferenceAt(),
                conference.getCapacity(),
                conference.getHasSessions(),
                List.of(SessionDto.fromEntity(session))
        );

        given(conferenceRepository.findWithSessionsById(conference.getId()))
                .willReturn(Optional.of(conference));
        given(conferenceMapper.toConferenceDetailResponse(conference))
                .willReturn(detailResponse);

        ConferenceDetailResponse foundResponse = conferenceQueryService.getConference(conference.getId());

        assertThat(foundResponse.getId()).isEqualTo(conference.getId());
        assertThat(foundResponse.getSessions()).hasSize(1);
    }

    @Test
    @DisplayName("컨퍼런스의 세션 목록 조회를 성공한다")
    void getSessionsByConferenceId_Success() {
        Conference conference = ConferenceFixture.컨퍼런스_아이디포함();
        Session session = SessionFixture.세션_아이디포함(conference);
        conference.getSessions().add(session);

        SessionResponse sessionResponse = new SessionResponse(
                session.getId(),
                session.getName(),
                session.getCapacity(),
                session.getLocation(),
                session.getTime(),
                session.getSummary(),
                session.getSpeakerName(),
                session.getSpeakerOrganization(),
                session.isActive()
        );

        given(conferenceRepository.findWithSessionsById(conference.getId()))
                .willReturn(Optional.of(conference));
        given(conferenceMapper.toSessionResponse(session))
                .willReturn(sessionResponse);

        List<SessionResponse> responses = conferenceQueryService.getSessionsByConferenceId(conference.getId());

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(session.getId());
    }

    @Test
    @DisplayName("세션 ID로 세션 단건 조회를 성공한다")
    void getSessionById_Success() {
        Conference conference = ConferenceFixture.컨퍼런스_아이디포함();
        Session session = SessionFixture.세션_아이디포함(conference);

        given(sessionRepository.findById(session.getId()))
                .willReturn(Optional.of(session));

        Session foundSession = conferenceQueryService.getSessionById(session.getId());

        assertThat(foundSession.getId()).isEqualTo(session.getId());
    }

    @Test
    @DisplayName("특정 컨퍼런스 내 특정 세션 상세 조회를 성공한다")
    void getSessionDetail_Success() {
        Conference conference = ConferenceFixture.컨퍼런스_아이디포함();
        Session session = SessionFixture.세션_아이디포함(conference);
        conference.getSessions().add(session);

        given(conferenceRepository.findWithSessionsById(conference.getId()))
                .willReturn(Optional.of(conference));
        given(sessionRepository.findById(session.getId()))
                .willReturn(Optional.of(session));

        Session resultSession = conferenceQueryService.getSessionDetail(conference.getId(), session.getId());

        assertThat(resultSession.getId()).isEqualTo(session.getId());
    }
}