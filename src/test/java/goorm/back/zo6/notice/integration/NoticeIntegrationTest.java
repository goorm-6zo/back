package goorm.back.zo6.notice.integration;


import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.infrastructure.ConferenceJpaRepository;
import goorm.back.zo6.fixture.ConferenceFixture;
import goorm.back.zo6.notice.application.NoticeService;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.infrastructure.ReservationJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class NoticeIntegrationTest {

    @Autowired
    NoticeService noticeService;
    @Autowired
    ReservationJpaRepository reservationRepository;
    @Autowired
    ConferenceJpaRepository conferenceRepository;

    /*@Test
    @DisplayName("컨퍼런스의 참여자에게 문자 메세지를 전송합니다.")
    public void sendMessageTest(){
        Conference conference = ConferenceFixture.컨퍼런스();
        conferenceRepository.save(conference);
        Reservation reservation1 = Reservation.builder().conference(conference).name("참가자1").phone("").build();
        Reservation reservation2 = Reservation.builder().conference(conference).name("참가자2").phone("").build();
        Reservation reservation3 = Reservation.builder().conference(conference).name("참가자2").phone("").build();
        reservationRepository.saveAll(List.of(reservation1, reservation2, reservation3));
        noticeService.sendMessage("테스트 메시지\n6조 화이팅",conference.getId(),null,"ALL");
    }

    @Test
    @DisplayName("컨퍼런스 미참여자에게 문자 메세지를 전송합니다.")
    public void sendMessageToNonAttendeeTest(){
        Conference conference = ConferenceFixture.컨퍼런스();
        conferenceRepository.save(conference);
        Reservation reservation1 = Reservation.builder().conference(conference).name("참가자1").phone("").build();
        //Reservation reservation2 = Reservation.builder().conference(conference).name("참가자2").phone("010-0000-0002").build();
        reservationRepository.saveAll(List.of(reservation1));
        noticeService.sendMessage("테스트 메시지\n6조 화이팅",conference.getId(),null,"NON_ATTENDEE");
    }*/
}
