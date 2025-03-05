package goorm.back.zo6.conference.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Entity
@Table(name = "conference")
public class Conference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conference_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "has_sessions", nullable = false)
    private Boolean hasSessions;

    @OneToMany(mappedBy = "conference", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Session> sessions;

    public Boolean containsSession(Long sessionId) {
        return this.sessions.stream().anyMatch(session -> session.getId().equals(sessionId));
    }

}
