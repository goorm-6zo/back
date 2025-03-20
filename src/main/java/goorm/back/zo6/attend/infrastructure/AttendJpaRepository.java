package goorm.back.zo6.attend.infrastructure;

import goorm.back.zo6.attend.domain.Attend;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendJpaRepository extends JpaRepository<Attend, Long> {

    @Query("""
    SELECT r.id, 
           COALESCE(rs.id, NULL), 
           r.conference.id, 
           COALESCE(s.id, NULL)
    FROM Reservation r
    LEFT JOIN r.reservationSessions rs ON (:sessionId IS NOT NULL)
    LEFT JOIN rs.session s ON (:sessionId IS NOT NULL)
    WHERE r.phone = :phone
    AND r.conference.id = :conferenceId
    AND (:sessionId IS NULL OR s.id = :sessionId)
""")
    List<Tuple> findAttendData(@Param("phone") String phone,
                               @Param("conferenceId") Long conferenceId,
                               @Param("sessionId") Long sessionId);

    @Query("""
    SELECT c.id, c.name, c.description, c.location, c.conferenceAt, c.capacity, c.hasSessions, c.imageKey, c.isActive,
           CASE WHEN MAX(a.id) IS NOT NULL THEN true ELSE false END, 
           s.id, s.name, s.capacity, s.location, s.time, s.summary, s.speakerName, s.speakerOrganization, s.isActive,
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