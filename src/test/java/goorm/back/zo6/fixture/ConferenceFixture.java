package goorm.back.zo6.fixture;

import goorm.back.zo6.conference.domain.Conference;

import java.util.HashSet;

public class ConferenceFixture {

    public static Conference 컨퍼런스() {
        return Conference.builder()
                .name("테스트 컨퍼런스")
                .hasSessions(true)
                .sessions(new HashSet<>())
                .build();
    }
}