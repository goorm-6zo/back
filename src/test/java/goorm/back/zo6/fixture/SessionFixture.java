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
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .summary("테스트 세션 요약")
                .speakerName("발표자")
                .speakerOrganization("발표자 소속")
                .isActive(true)
                .speakerImageKey("test.png")
                .build();
    }

    public static Session 세션_아이디포함(Conference conference) {
        return Session.builder()
                .id(1L)
                .name("테스트 세션")
                .conference(conference)
                .capacity(100)
                .location("온라인")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .summary("테스트 세션 요약")
                .speakerName("발표자")
                .speakerOrganization("발표자 소속")
                .isActive(true)
                .speakerImageKey("test.png")
                .build();
    }
}