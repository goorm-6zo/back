package goorm.back.zo6.fixture;
import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;

public class UserFixture {
    public static User 유저() {
        return User.builder()
                .name("홍길순")
                .email("test@gmail.com")
                .phone("01011112222")
                .birthDate("2000-10-20")
                .role(Role.of("USER"))
                .build();
    }
}