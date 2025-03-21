package goorm.back.zo6.fixture;

import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;

public class AdminFixture {
    public static User 관리자() {
        return User.builder()
                .name("홍길순")
                .email("test@gmail.com")
                .phone("01011112222")
                .role(Role.of("ADMIN"))
                .build();
    }
}
