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

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "summary", nullable = false)
    private String summary;

    @Column(name = "speaker_name")
    private String speakerName;

    @Column(name = "speaker_organization")
    private String speakerOrganization;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "speaker_image_key")
    private String speakerImageKey;

    public Session(Conference conference, String name, Integer capacity, String location, LocalDateTime startTime, LocalDateTime endTime, String summary, String speakerName, String speakerOrganization) {
        if (conference == null || conference.getId() == null) {
            throw new CustomException(ErrorCode.CONFERENCE_NOT_FOUND);
        }
        if (startTime == null || endTime == null || endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new CustomException(ErrorCode.INVALID_SESSION_TIME);
        }
        this.conference = conference;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.summary = summary;
        this.speakerName = speakerName;
        this.speakerOrganization = speakerOrganization;
        this.isActive = true;
    }

    public void updateSession(String location) {
        if (location == null || location.isBlank())
            throw new CustomException(ErrorCode.INVALID_SESSION_LOCATION);

        this.location = location;
    }

    public boolean isReservable() { return capacity > 0; }

    public void setConference(Conference conference) {
        if (conference == null || conference.getId() == null) {
            throw new CustomException(ErrorCode.CONFERENCE_NOT_FOUND);
        }
        this.conference = conference;
    }

    public void updateActive(boolean active) {
        this.isActive = active;
    }
}