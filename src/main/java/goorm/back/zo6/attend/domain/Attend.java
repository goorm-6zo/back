package goorm.back.zo6.attend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "attend")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Attend {
    @Id @GeneratedValue
    Long id;
    @Column(nullable = false)
    Long reservationId;
    @Column()
    Long reservationSession;
    @Column(nullable = false)
    Long userId;
    public static Attend of(Long reservationId, Long userId){
        return Attend.builder()
                .reservationId(reservationId)
                .userId(userId)
                .build();
    }
}
