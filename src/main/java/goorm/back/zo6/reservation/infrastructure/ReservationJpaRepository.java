package goorm.back.zo6.reservation.infrastructure;

import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationStatus;
import goorm.back.zo6.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findAllByNameAndPhone(String name, String phone);

    List<Reservation> findAllByPhoneAndStatus(String phone, ReservationStatus status);

    List<Reservation> findAllByUser(User user);

    List<Reservation> findByConferenceIdAndUserId(Long conferenceId, Long userId);

    @Query(value = """
    SELECT EXISTS (
        SELECT 1 FROM reservation
        WHERE user_id = :userId
        AND conference_id = :conferenceId
        AND status = 'CONFIRMED'
    )
""", nativeQuery = true)
    boolean existsByUserAndConference(
            @Param("userId") Long userId,
            @Param("conferenceId") Long conferenceId
    );

    @Query(value = """
    SELECT EXISTS (
        SELECT 1 
        FROM reservation r
        JOIN reservation_session rs ON r.reservation_id = rs.reservation_id
        WHERE r.user_id = :userId
        AND r.conference_id = :conferenceId
        AND rs.session_id = :sessionId
        AND r.status = 'CONFIRMED'
    )
""", nativeQuery = true)
    boolean existsByUserAndConferenceAndSession(
            @Param("userId") Long userId,
            @Param("conferenceId") Long conferenceId,
            @Param("sessionId") Long sessionId
    );

}
