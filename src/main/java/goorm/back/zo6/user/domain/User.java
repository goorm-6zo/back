package goorm.back.zo6.user.domain;

import goorm.back.zo6.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Builder
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 빌더 유도
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DynamicUpdate //JPA의 Dirty Checking을 사용할 경우, 모든 필드에 대해 UPDATE 쿼리가 나간다.
@Getter
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Embedded
    private Password password;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "birth_date")
    private String birthDate;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public static User singUpUser(String email, String name, String password, String phone, String birthDate, Role role){
        return User.builder()
                .email(email)
                .name(name)
                .password(Password.from(password))
                .phone(phone)
                .birthDate(birthDate)
                .role(role)
                .build();
    }
}
