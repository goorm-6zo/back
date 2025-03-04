package goorm.back.zo6.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Embeddable
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Password {
    @Column(name = "password")
    private String value;

    public static Password from(String value){
        return Password.builder().value(value).build();
    }
}
