package goorm.back.zo6.reservation.domain;

import goorm.back.zo6.conference.domain.Conference;
import goorm.back.zo6.conference.domain.Session;
import goorm.back.zo6.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "reservation")
public class Reservation {

    @Id
    @Column(name = "reservation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conference_id", nullable = false)
    private Conference conference;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ReservationSession> reservationSessions = new HashSet<>();

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Reservation(Conference conference, String name, String phone) {
        this.conference = conference;
        this.name = name;
        this.phone = phone;
        this.status = (status != null) ? status : ReservationStatus.TEMPORARY;
    }

    public void linkUser(User user) {
        if (this.user != null) {
            throw new IllegalStateException("이미 사용자와 연결되어 있습니다.");
        }
        this.user = user;
    }

    public boolean isPhoneMatched(String inputPhone) {
        return this.phone.equals(inputPhone);
    }

    public void confirm() {
        if (this.status != ReservationStatus.TEMPORARY) {
            throw new IllegalStateException("임시 상태의 예약만 확정 가능합니다.");
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    public void confirmReservation() {
        if (this.status == ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("이미 확정된 예약입니다.");
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    public void addSession(Session session) {
        this.reservationSessions.add(new ReservationSession(this, session));
    }

}
