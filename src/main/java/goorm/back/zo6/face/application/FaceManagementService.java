package goorm.back.zo6.face.application;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.face.domain.Face;
import goorm.back.zo6.face.domain.FaceRepository;
import goorm.back.zo6.face.dto.response.FaceResponse;
import goorm.back.zo6.face.infrastructure.RekognitionApiClient;
import goorm.back.zo6.face.infrastructure.S3FaceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class FaceManagementService {

    private final S3FaceClient s3FaceClient; // s3에 저장하기 위함
    private final RekognitionApiClient rekognitionApiClient; // rekognition collection 에 저장하기 위함
    private final FaceRepository faceRepository;

    @Value("${amazon.aws.s3.bucket-name}")
    private String bucketName;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");

    // 얼굴 이미지 저장, S3 저장 및 collection 저장
    @Transactional
    public FaceResponse uploadUserFace(Long userId, MultipartFile faceImage){
        String fileExtension = getFileExtension(faceImage.getOriginalFilename());
        String imageKey = makeImageKey(userId, fileExtension);

        // s3에 유저 얼굴 이미지 업로드
        s3FaceClient.uploadFile(faceImage, imageKey, bucketName);
        
        // Rekognition Collection 에 유저 얼굴 등록
        String rekognitionFaceId = rekognitionApiClient.addFaceToCollection(imageKey, userId, bucketName);

        Face face = faceRepository.save(Face.of(imageKey, rekognitionFaceId, userId));
        log.info(" 얼굴 등록 완료!, userId : {}, Face ID: {}", userId, rekognitionFaceId);
        return FaceResponse.from(face);
    }

    // 얼굴 이미지 다운로드 url 반환
    public String getFaceImageUrl(Long userId){
        Face face = faceRepository.findFaceIdByUserId(userId);
        String imageKey = face.getImageKey();
        String getUrl = s3FaceClient.generateDownloadPreSignedUrl(imageKey, bucketName);

        log.info("s3 이미지 조회 PresingedUrl 생성에 성공하였습니다.");
        return getUrl;
    }

    // 얼굴 이미지 삭제, s3 이미지 삭제, rekognition collection 이미지 삭제
    @Transactional
    public void deleteFaceImage(Long userId) {
        Face face = faceRepository.findFaceIdByUserId(userId);
        String imageKey = face.getImageKey();
        String rekognitionId = face.getRekognitionFaceId();

        // S3에 저장된 이미지 삭제
        s3FaceClient.deleteFaceImage(imageKey, bucketName);
        // Rekognition Collection 에 저장된 이미지 삭제
        rekognitionApiClient.deleteFaceFromCollection(rekognitionId);
        // DB 에서 삭제
        faceRepository.deleteByUserId(userId);

        log.info("얼굴 데이터 삭제 완료! userId : {}", userId);
    }

    private String makeImageKey(Long userId, String extension) {
        return String.format("images/faces/%d/face.%s",userId,extension);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            throw new CustomException(ErrorCode.INVALID_FILE_NAME);
        }
        // 확장자 분리
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if(!ALLOWED_EXTENSIONS.contains(extension)){
            throw new CustomException(ErrorCode.UNSUPPORTED_FILE_EXTENSION);
        }
        return extension;
    }
}