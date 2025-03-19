package goorm.back.zo6.attend.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "attend")
public class Attend {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Long userId;
    private Long reservationId;
    private Long reservationSessionId;
    private Long conferenceId;
    private Long sessionId;

    public static Attend of(Long userId, Long reservationId, Long reservationSessionId, Long conferenceId, Long sessionId){
        return Attend.builder()
                .userId(userId)
                .reservationId(reservationId)
                .reservationSessionId(reservationSessionId)
                .conferenceId(conferenceId)
                .sessionId(sessionId)
                .build();
    }
}
