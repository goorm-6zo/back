package goorm.back.zo6.attend.domain;

import goorm.back.zo6.face.domain.Face;

public interface AttendRepository {
    Attend save(Attend attend);
    void deleteByUserId(Long userId);
    Attend findFaceIdByUserId(Long userId);
}
