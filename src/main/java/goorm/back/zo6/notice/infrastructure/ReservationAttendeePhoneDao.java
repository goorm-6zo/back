package goorm.back.zo6.notice.infrastructure;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ReservationAttendeePhoneDao {

    private final EntityManager entityManager;


    public List<String> getPhoneConferenceAttendee(Long conferenceId) {
        String sql = "SELECT phone FROM reservation WHERE conference_id = :conference_id";
        List<String> result = entityManager.createNativeQuery(sql).setParameter("conference_id", conferenceId).getResultList();
        return result;
    }

    public List<String> getPhoneSessionAttendee(Long conferenceId, Long sessionId) {
        String sql = "SELECT r.phone FROM reservation_session rs " +
                "JOIN reservation r ON rs.reservation_id = r.reservation_id " +
                "WHERE rs.session_id = :session_id AND r.conference_id = :conference_id";

        List<String> result= entityManager.createNativeQuery(sql)
                .setParameter("session_id", sessionId)
                .setParameter("conference_id", conferenceId)
                .getResultList();
        return result;
    }

    public List<String> getPhoneByUserId(Set<String> ids) {
        String sql = "SELECT phone FROM users WHERE user_id IN (:ids)";

        return entityManager.createNativeQuery(sql)
                .setParameter("ids", ids)
                .getResultList();
    }
}
