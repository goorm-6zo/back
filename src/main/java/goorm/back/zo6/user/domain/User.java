package goorm.back.zo6.user.domain;

import goorm.back.zo6.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Builder
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 빌더 유도
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@SQLDelete(sql = "UPDATE  users SET is_deleted = true WHERE user_id = ?") // soft delete
@SQLRestriction("is_deleted = false")
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "name")
    private String name;

    @Embedded
    private Password password;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public static User singUpUser(String email, String name, String password, String phone, Role role){
        return User.builder()
                .email(email)
                .name(name)
                .password(Password.from(password))
                .phone(phone)
                .role(role)
                .build();
    }

    public void updatePhoneNumber(String phoneNumber){
        this.phone = phoneNumber;
    }

    public void updateNickname(String nickname) {
        this.name = nickname;
    }

    public void logicalDelete() {
        this.isDeleted = true;
    }
}
