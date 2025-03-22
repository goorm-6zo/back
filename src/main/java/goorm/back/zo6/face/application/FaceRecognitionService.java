package goorm.back.zo6.face.application;

import goorm.back.zo6.common.event.Events;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.face.domain.Face;
import goorm.back.zo6.face.domain.FaceRepository;
import goorm.back.zo6.face.dto.response.CollectionResponse;
import goorm.back.zo6.face.dto.response.FaceAuthResultResponse;
import goorm.back.zo6.face.dto.response.FaceMatchingResponse;
import goorm.back.zo6.face.dto.response.FaceResponse;
import goorm.back.zo6.face.infrastructure.RekognitionApiClient;
import goorm.back.zo6.attend.domain.AttendEvent;
import goorm.back.zo6.reservation.domain.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class FaceRecognitionService {
    private final RekognitionApiClient rekognitionApiClient;
    private final FaceRepository faceRepository;
    private final ReservationRepository reservationRepository;

    // 얼굴 데이터 collection 저장
    @Transactional
    public FaceResponse uploadUserFace(Long userId, MultipartFile faceImage){
        // 이미지 데이터를 Rekognition Collection 에 등록
        byte[] imageBytes = toBytes(faceImage);
        String rekognitionFaceId = rekognitionApiClient.addFaceToCollection(userId, imageBytes);

        // DB에 얼굴 정보 저장
        Face face = faceRepository.save(Face.of(rekognitionFaceId, userId));
        log.info("얼굴 등록 완료! userId: {}, Face ID: {}", userId, rekognitionFaceId);

        return FaceResponse.from(face);
    }

    // 얼굴 데이터 collection 에서 삭제
    @Transactional
    public void deleteUserFace(Long userId) {
        Face face = faceRepository.findFaceByUserId(userId);
        String rekognitionId = face.getRekognitionFaceId();
        // Rekognition Collection 에 저장된 이미지 삭제
        rekognitionApiClient.deleteFaceFromCollection(rekognitionId);
        // DB 에서 삭제
        faceRepository.deleteByUserId(userId);
        log.info("얼굴 데이터 삭제 완료! userId : {}", userId);
    }

    // 얼굴 비교 및 인증
    @Transactional
    public FaceAuthResultResponse authenticationByUserFace(Long conferenceId, Long sessionId, MultipartFile uploadedFile) {
        // 전달 된 얼굴 이미지를 ByteBuffer 로 변환
        ByteBuffer imageBytes = ByteBuffer.wrap(toBytes(uploadedFile));
        // collection 에 존재하는 얼굴 이미지와 전달 된 이미지 비교 결과
        FaceMatchingResponse response = rekognitionApiClient.authorizeUserFace(imageBytes);
        if(response == null){
            return new FaceAuthResultResponse();
        }

        Long userId = Long.parseLong(response.userId());

        // 여기서 해당 유저가 예매를 한 유저인지 판단 후 예외 던지기
        validateReservation(userId, conferenceId, sessionId);

        // 얼굴 인증 후 참가 이벤트 발생
        Events.raise(new AttendEvent(userId, conferenceId, sessionId));

        return new FaceAuthResultResponse(response.userId(), response.similarity());
    }

    // rekognition collection 생성, 초기 1회 실행
    public CollectionResponse createCollection(){
        String collectionArl = rekognitionApiClient.createCollection();
        return CollectionResponse.of(collectionArl);
    }

    private byte[] toBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_CONVERSION_EXCEPTION);
        }
    }

    private void validateReservation(Long userId, Long conferenceId, Long sessionId) {
        boolean isReserved = (sessionId == null)
                ? reservationRepository.existsByUserIdAndConferenceId(userId, conferenceId)
                : reservationRepository.existsByUserAndConferenceAndSession(userId, conferenceId, sessionId);

        log.info("isReserved : {}", isReserved);
        if(!isReserved) {
            throw new CustomException(ErrorCode.USER_NOT_RESERVED);
        }
    }
}