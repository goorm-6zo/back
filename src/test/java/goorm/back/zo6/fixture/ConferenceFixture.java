package goorm.back.zo6.fixture;

import goorm.back.zo6.conference.domain.Conference;

import java.time.LocalDateTime;
import java.util.HashSet;

public class ConferenceFixture {

    public static Conference 컨퍼런스() {
        return Conference.builder()
                .name("테스트 컨퍼런스")
                .description("테스트 컨퍼런스 소개")
                .location("테스트 주소 1234")
                .conferenceAt(LocalDateTime.now())
                .capacity(100)
                .hasSessions(true)
                .sessions(new HashSet<>())
                .build();
    }
}