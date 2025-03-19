package goorm.back.zo6.user.domain;

import goorm.back.zo6.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 빌더 유도
@Getter
@SQLDelete(sql = "UPDATE  users SET is_deleted = true WHERE user_id = ?") // soft delete
@SQLRestriction("is_deleted = false")
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

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    private User(String email, String name, Password password, String phone, Role role) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.isDeleted = false;
    }
    public static User singUpUser(String email, String name, String password, String phone, Role role) {
        return User.builder()
                .email(email)
                .name(name)
                .password(Password.from(password))
                .phone(phone)
                .role(role)
                .build();
    }

    public void logicalDelete() {
        this.isDeleted = true;
    }
}
