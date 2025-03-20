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

    // 얼굴 데이터 collection 저장
    @Transactional
    public FaceResponse uploadUserFace(Long userId, MultipartFile faceImage){
        try {
            // 이미지 데이터를 Rekognition Collection에 등록
            byte[] imageBytes = faceImage.getBytes();
            String rekognitionFaceId = rekognitionApiClient.addFaceToCollection(userId, imageBytes);

            // DB에 얼굴 정보 저장
            Face face = faceRepository.save(Face.of(rekognitionFaceId, userId));
            log.info("얼굴 등록 완료! userId: {}, Face ID: {}", userId, rekognitionFaceId);

            return FaceResponse.from(face);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_CONVERSION_EXCEPTION);
        }
    }

    // 얼굴 데이터 collection  삭제
    @Transactional
    public void deleteFaceImage(Long userId) {
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
        try {
            // 전달 된 얼굴 이미지를 ByteBuffer 로 변환
            ByteBuffer imageBytes = ByteBuffer.wrap(uploadedFile.getBytes());
            // collection 에 존재하는 얼굴 이미지와 전달 된 이미지 비교 결과
            FaceMatchingResponse response = rekognitionApiClient.authorizeUserFace(imageBytes);

            log.info("유저 {} 얼굴정보 확인, 유사도 : {}", response.userId(), response.similarity());

            // 얼굴 인증 후 참가 이벤트 발생
            Events.raise(new AttendEvent(Long.parseLong(response.userId()), conferenceId, sessionId));
            return FaceAuthResultResponse.of(response.userId(), response.similarity());

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_CONVERSION_EXCEPTION);
        }
    }

    // rekognition collection 생성, 초기 1회 실행
    public CollectionResponse createCollection(){
        String collectionArl = rekognitionApiClient.createCollection();
        return CollectionResponse.of(collectionArl);
    }
}