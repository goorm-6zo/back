package goorm.back.zo6.reservation.infrastructure;

import goorm.back.zo6.reservation.domain.ReservationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationSessionJpaRepository extends JpaRepository<ReservationSession, Long> {

    @Query("SELECT COUNT(rs) FROM ReservationSession rs WHERE rs.session.id = :sessionId")
    Long countBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT CASE WHEN COUNT(rs) > 0 THEN TRUE ELSE FALSE END FROM ReservationSession rs " + "WHERE rs.session.id = :sessionId AND rs.reservation.name = :name AND rs.reservation.phone = :phone")
    Boolean existsBySessionIdAndNameAndPhone(@Param("sessionId") Long sessionId, @Param("name") String name, @Param("phone") String phone);
}
