package goorm.back.zo6.conference.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
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

    @Column(name = "conference_at", nullable = false)
    private LocalDateTime conferenceAt;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "has_sessions", nullable = false)
    private Boolean hasSessions;

    @OneToMany(mappedBy = "conference", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Session> sessions = new HashSet<>();

    public void addSession(Session session) {
        session.setConference(this);
        this.sessions.add(session);
    }

    public void removeSession(Session session) {
        this.sessions.remove(session);
        session.setConference(null);
    }

    public Boolean containsSession(Long sessionId) {
        return this.sessions.stream().anyMatch(session -> session.getId().equals(sessionId));
    }

    public void validateSessionOwnership(Set<Long> sessionIds) {
        if (!hasSessions) {
            throw new IllegalStateException("This conference does not have sessions");
        }

        Set<Long> existingSessionIds = this.sessions.stream()
                .map(Session::getId)
                .collect(Collectors.toSet());

        if (!existingSessionIds.containsAll(sessionIds)) {
            throw new IllegalStateException("This conference does not have all the sessions");
        }
    }

    public Set<Long> getSessionIds() {
        return this.sessions.stream()
                .map(Session::getId)
                .collect(Collectors.toSet());
    }

    public boolean hasReservablesSessions() {
        return this.sessions.stream()
                .anyMatch(Session::isReservable);
    }

    public boolean isReservableWithoutSessions() {
        return !hasSessions || !hasReservablesSessions();
    }

    public void validateCapacity(int currentParticipantCount) {
        if (currentParticipantCount > capacity) {
            throw new IllegalStateException("Conference capacity exceeded. Max capacity: " + capacity);
        }
    }

    public boolean canAccommodate(int currentParticipantCount) {
        return currentParticipantCount < capacity;
    }
}
