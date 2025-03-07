package goorm.back.zo6.fixture;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;

import java.time.LocalDateTime;

public class SessionFixture {

    public static Session 세션(Conference conference) {
        return Session.builder()
                .conference(conference)
                .name("테스트 세션")
                .capacity(100)
                .location("온라인")
                .time(LocalDateTime.now())
                .summary("테스트 세션 요약")
                .build();
    }
}