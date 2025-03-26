package goorm.back.zo6.face.infrastructure;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.face.domain.Face;
import goorm.back.zo6.face.domain.FaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FaceRepositoryImpl implements FaceRepository {
    private final FaceJpaRepository faceJpaRepository;
    @Override
    public Face save(Face face) {
        return faceJpaRepository.save(face);
    }

    @Override
    public void deleteByUserId(Long userId) {
        faceJpaRepository.deleteByUserId(userId);
    }

    @Override
    public Optional<Face> findFaceByUserId(Long userId) {
        return faceJpaRepository.findFaceByUserId(userId);
    }

}
