package goorm.back.zo6.qr.infrastructure;

import goorm.back.zo6.qr.domain.IdExistenceDao;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SessionIdExistenceDao implements IdExistenceDao {
    private final EntityManager entityManager;

    @Override
    public boolean exists(Long id) {
        String sql = "SELECT 1 FROM session WHERE session_id = :id";
        List<?> result = entityManager.createNativeQuery(sql).setParameter("id", id).getResultList();

        return !result.isEmpty();
    }
}
