package goorm.back.zo6.conference.domain;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
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

    @Column(name = "speaker_name", nullable = false)
    private String speakerName;

    @Column(name = "speaker_organization", nullable = false)
    private String speakerOrganization;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    public Session(Conference conference, String name, Integer capacity, String location, LocalDateTime time, String summary, String speakerName, String speakerOrganization) {
        if (conference == null) {
            throw new CustomException(ErrorCode.CONFERENCE_NOT_FOUND);
        }
        this.conference = conference;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.time = time;
        this.summary = summary;
        this.speakerName = speakerName;
        this.speakerOrganization = speakerOrganization;
        this.isActive = true;
    }

    public void updateSession(String name, Integer capacity, String location, LocalDateTime time, String summary, String speakerName, String speakerOrganization, boolean isActive) {
        if (capacity < 0) {
            throw new CustomException(ErrorCode.INVALID_SESSION_CAPACITY);
        }
        if (time.isBefore(LocalDateTime.now())) {
            throw new CustomException(ErrorCode.INVALID_SESSION_TIME);
        }
        if (name == null || name.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_SESSION_NAME);
        }
        if (location == null || location.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_SESSION_LOCATION);
        }
        if (speakerName == null || speakerName.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_SESSION_NAME);
        }
        if (speakerOrganization == null || speakerOrganization.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_SESSION_NAME);
        }

        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.time = time;
        this.summary = summary;
        this.speakerName = speakerName;
        this.speakerOrganization = speakerOrganization;
        this.isActive = isActive;
    }

    public boolean isReservable() { return capacity > 0; }

    public void setConference(Conference conference) {
        if (conference == null) {
            throw new CustomException(ErrorCode.CONFERENCE_NOT_FOUND);
        }
        this.conference = conference;
    }
}