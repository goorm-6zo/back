package goorm.back.zo6.conference.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "session")
public class Session {

    @Id
    @Column(name = "session_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conference_id", nullable = false)
    private Conference conference;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @Column(name = "summary", nullable = false)
    private String summary;

    public Session(Conference conference, String name, Integer capacity, String location, LocalDateTime time, String summary) {
        if (conference == null) {
            throw new IllegalArgumentException("conferenceId must not be null");
        }
        this.conference = conference;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.time = time;
        this.summary = summary;
    }

    public boolean isReservable() { return capacity > 0; }

    public void reserveSeat() {
        if (this.capacity <= 0) {
            throw new IllegalStateException("No available seats for this session");
        }
        this.capacity--;
    }

    public void setConference(Conference conference) {
        if (conference == null) {
            throw new IllegalArgumentException("conferenceId must not be null");
        }
        this.conference = conference;
    }
}