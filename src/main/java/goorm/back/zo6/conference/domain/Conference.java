package goorm.back.zo6.conference.domain;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "conference")
public class Conference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conference_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "area", nullable = false)
    private String area;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "image_key", nullable = false)
    private String imageKey;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "has_sessions", nullable = false)
    private Boolean hasSessions;

    @OneToMany(mappedBy = "conference", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Session> sessions = new ArrayList<>();

    public void addSession(Session session) {
        if (this.sessions.stream().anyMatch(existingSession -> session.getId() != null && session.getId().equals(existingSession.getId()))) {
            return;
        }
        session.setConference(this);
        this.sessions.add(session);
    }

    public Boolean containsSession(Long sessionId) {
        return this.sessions.stream().anyMatch(session -> session.getId().equals(sessionId));
    }

    public void validateSessionOwnership(Set<Long> sessionIds) {
        if (!hasSessions) {
            throw new CustomException(ErrorCode.CONFERENCE_NOT_FOUND);
        }

        Set<Long> existingSessionIds = this.sessions.stream()
                .map(Session::getId)
                .collect(Collectors.toSet());

        if (!existingSessionIds.containsAll(sessionIds)) {
            throw new CustomException(ErrorCode.SESSION_NOT_BELONG_TO_CONFERENCE);
        }
    }

    public void updateActive(boolean active) {
        this.isActive = active;
    }
}
