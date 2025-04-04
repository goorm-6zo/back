package goorm.back.zo6.face.domain;

import java.util.Optional;

public interface FaceRepository {
    Face save(Face face);
    void deleteByUserId(Long userId);
    Optional<Face> findFaceByUserId(Long userId);
}
