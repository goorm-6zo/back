package goorm.back.zo6.face.infrastructure;

import goorm.back.zo6.face.domain.Face;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FaceJpaRepository extends JpaRepository<Face, Long> {
    void deleteByUserId(Long userId);
    Optional<Face> findFaceByUserId(Long userId);

}
