package goorm.back.zo6.attend.infrastructure;

import goorm.back.zo6.attend.domain.Attend;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendJpaRepository extends JpaRepository<Attend, Long> {
    @Modifying
    @Query("DELETE FROM Attend a WHERE a.userId = :userId AND a.reservationId = :reservationId AND " +
            "(a.reservationSessionId = :reservationSessionId OR (:reservationSessionId IS NULL AND a.reservationSessionId IS NULL))")
    void deleteByUserIdAndReservationIdAndReservationSessionId(@Param("userId") Long userId,
                                                   @Param("reservationId") Long reservationId,
                                                   @Param("reservationSessionId") Long reservationSessionId);

    List<Attend> findByUserIdAndConferenceId(Long userId, Long conferenceId);

    @Query("""
    SELECT c.id, c.name, c.description, c.location, c.conferenceAt, c.capacity, c.hasSessions,
           CASE WHEN MAX(a.id) IS NOT NULL THEN true ELSE false END, 
           s.id, s.name, s.capacity, s.location, s.time, s.summary,
           CASE WHEN MAX(a.sessionId) IS NOT NULL THEN true ELSE false END
    FROM Reservation r
    JOIN r.conference c
    LEFT JOIN r.reservationSessions rs
    LEFT JOIN rs.session s
    LEFT JOIN Attend a 
        ON a.userId = :userId 
        AND a.reservationId = r.id 
        AND (a.sessionId = s.id OR a.sessionId IS NULL) 
    WHERE r.phone = (SELECT u.phone FROM User u WHERE u.id = :userId) 
      AND r.conference.id = :conferenceId
      AND r.status = 'CONFIRMED'
    GROUP BY c.id, c.name, c.description, c.location, c.conferenceAt, c.capacity, c.hasSessions, 
             s.id, s.name, s.capacity, s.location, s.time, s.summary
""")
    List<Tuple> findAttendInfoByUserAndConference(
            @Param("userId") Long userId,
            @Param("conferenceId") Long conferenceId
    );
}