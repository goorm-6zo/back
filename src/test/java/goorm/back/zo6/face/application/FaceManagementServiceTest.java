package goorm.back.zo6.face.application;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.face.domain.Face;
import goorm.back.zo6.face.domain.FaceRepository;
import goorm.back.zo6.face.dto.response.FaceResponse;
import goorm.back.zo6.face.infrastructure.RekognitionApiClient;
import goorm.back.zo6.face.infrastructure.S3FaceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FaceManagementServiceTest {
    @InjectMocks
    private FaceManagementService faceManagementService;
    @Mock
    private FaceRepository faceRepository;
    @Mock
    private S3FaceClient s3FaceClient;
    @Mock
    private RekognitionApiClient rekognitionApiClient;
    private String bucketName;

    @BeforeEach
    void setUp(){
        bucketName = "test-bucket-name";
        // Value 값 직접 주입
        ReflectionTestUtils.setField(faceManagementService, "bucketName", "test-bucket-name");
    }

    @Test
    @DisplayName("얼굴 이미지 업로드 - 성공")
    void uploadUserFace_Success() {
        // given
        Long userId = 1L;
        String imageKey = "images/faces/1/face.jpg";
        String rekognitionFaceId = "rekog-12345";
        Face face = Face.of(imageKey, rekognitionFaceId, userId);
        MultipartFile testFaceImage = mock(MultipartFile.class);

        when(testFaceImage.getOriginalFilename()).thenReturn("test.jpg");
        // s3 저장 성공
        doNothing().when(s3FaceClient).uploadFile(testFaceImage, imageKey, bucketName);
        // collection 저장 성공
        when(rekognitionApiClient.addFaceToCollection(imageKey, userId, bucketName)).thenReturn(rekognitionFaceId);
        // Face 객체 저장
        when(faceRepository.save(any(Face.class))).thenReturn(face);

        // when
        FaceResponse response = faceManagementService.uploadUserFace(userId, testFaceImage);

        // then
        assertNotNull(response);
        assertEquals(rekognitionFaceId, response.rekognitionId());
        assertEquals(imageKey, response.imageKey());

        verify(s3FaceClient, times(1)).uploadFile(testFaceImage,imageKey,bucketName);
        verify(rekognitionApiClient, times(1)).addFaceToCollection(imageKey, userId, bucketName);
        verify(faceRepository, times(1)).save(any(Face.class));
    }
    @Test
    @DisplayName("얼굴 이미지 업로드 - S3 업로드가 실패했을 때 예외 발생")
    void uploadUserFace_WhenS3UploadFails() {
        // given
        Long userId = 1L;
        String imageKey = "images/faces/1/face.jpg";
        MultipartFile testFaceImage = mock(MultipartFile.class);
        when(testFaceImage.getOriginalFilename()).thenReturn("face.jpg");

        // S3 업로드 실패하도록 설정
        doThrow(new CustomException(ErrorCode.FACE_UPLOAD_FAIL))
                .when(s3FaceClient).uploadFile(eq(testFaceImage), eq(imageKey), eq(bucketName));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> faceManagementService.uploadUserFace(userId, testFaceImage));

        // then
        assertEquals(ErrorCode.FACE_UPLOAD_FAIL, exception.getErrorCode());
        // S3는 호출이 된다.
        verify(s3FaceClient, times(1)).uploadFile(testFaceImage, imageKey, bucketName);
        // faceRepository, rekognitionApiClient 는 호출되지 않는다.
        verifyNoInteractions(rekognitionApiClient, faceRepository);
    }

    @Test
    @DisplayName("얼굴 이미지 업로드 - Rekognition Collection 에 얼굴 이미지 등록이 실패할 경우 예외 발생")
    void uploadUserFace_WhenRekognitionFails() {
        // given
        Long userId = 1L;
        String imageKey = "images/faces/1/face.jpg";
        MultipartFile testFaceImage = mock(MultipartFile.class);
        when(testFaceImage.getOriginalFilename()).thenReturn("face.jpg");

        // S3 업로드 성공
        doNothing().when(s3FaceClient).uploadFile(eq(testFaceImage), eq(imageKey), eq(bucketName));

        // Rekognition 등록 실패
        doThrow(new CustomException(ErrorCode.FACE_UPLOAD_FAIL))
                .when(rekognitionApiClient).addFaceToCollection(eq(imageKey), eq(userId), eq(bucketName));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> faceManagementService.uploadUserFace(userId, testFaceImage));

        // then
        assertEquals(ErrorCode.FACE_UPLOAD_FAIL, exception.getErrorCode());
        // S3는 호출이 된다.
        verify(s3FaceClient, times(1)).uploadFile(testFaceImage, imageKey, bucketName);
        // Rekognition 호출이 된다.
        verify(rekognitionApiClient, times(1)).addFaceToCollection(imageKey, userId, bucketName);
        verifyNoInteractions(faceRepository);
    }


    @Test
    @DisplayName("얼굴 이미지 조회 - presigned url 생성 성공!")
    void getFaceImageUrl_Success() {
        // given
        Long userId = 1L;
        String imageKey = "images/faces/1/face.jpg";
        String expectedUrl = "https://s3.amazonaws.com/test-bucket-name/faces/1/face.jpg";
        String rekognitionFaceId = "rekog-12345";
        Face face = Face.of(imageKey, rekognitionFaceId, userId);

        when(faceRepository.findFaceIdByUserId(userId)).thenReturn(face);
        when(s3FaceClient.generateDownloadPreSignedUrl(imageKey, bucketName)).thenReturn(expectedUrl);

        // when
        String resultUrl = faceManagementService.getFaceImageUrl(userId);

        // then
        assertNotNull(resultUrl);
        assertEquals(expectedUrl, resultUrl);

        verify(faceRepository, times(1)).findFaceIdByUserId(userId);
        verify(s3FaceClient, times(1)).generateDownloadPreSignedUrl(imageKey, bucketName);
    }

    @Test
    @DisplayName("얼굴 이미지 조회 - presigned url 생성 성공!")
    void getFaceImageUrl_Fail() {
        // given
        Long userId = 1L;
        String imageKey = "images/faces/1/face.jpg";
        String expectedUrl = "https://s3.amazonaws.com/test-bucket-name/faces/1/face.jpg";
        String rekognitionFaceId = "rekog-12345";
        Face face = Face.of(imageKey, rekognitionFaceId, userId);

        when(faceRepository.findFaceIdByUserId(userId)).thenReturn(face);
        when(s3FaceClient.generateDownloadPreSignedUrl(imageKey, bucketName)).thenReturn(expectedUrl);

        // when
        String resultUrl = faceManagementService.getFaceImageUrl(userId);

        // then
        assertNotNull(resultUrl);
        assertEquals(expectedUrl, resultUrl);

        verify(faceRepository, times(1)).findFaceIdByUserId(userId);
        verify(s3FaceClient, times(1)).generateDownloadPreSignedUrl(imageKey, bucketName);
    }

    @Test
    @DisplayName("얼굴 이미지 조회 -  Face 객체 조회 실패시 예외 발생")
    void getFaceImageUrl_WhenFaceNotFoundFails() {
        // given
        Long userId = 1L;

        // FaceRepository에서 유저 얼굴 정보가 없을 경우 예외 발생하도록 설정
        doThrow(new CustomException(ErrorCode.FACE_NOT_FOUND))
                .when(faceRepository).findFaceIdByUserId(eq(userId));

        // when
        // 예외 발생 확인
        CustomException exception = assertThrows(CustomException.class, () -> faceManagementService.getFaceImageUrl(userId));

        // then
        assertEquals(ErrorCode.FACE_NOT_FOUND, exception.getErrorCode());
        // faceRepository 호출 검증
        verify(faceRepository, times(1)).findFaceIdByUserId(userId);
        // S3 Presigned URL 생성이 호출되지 않아야 함
        verifyNoInteractions(s3FaceClient);
    }

    @Test
    @DisplayName("얼굴 이미지 조회 - Presigned URL 생성 실패 시 예외 발생")
    void getFaceImageUrl_WhenPresignedUrlFails() {
        // given
        Long userId = 1L;
        String imageKey = "images/faces/1/face.jpg";
        String rekognitionFaceId = "rekog-12345";
        Face face = Face.of(imageKey, rekognitionFaceId, userId);

        // 정상적으로 객체 반환
        when(faceRepository.findFaceIdByUserId(userId)).thenReturn(face);

        // S3 Presigned URL 생성 실패하도록 설정
        doThrow(new CustomException(ErrorCode.PRESIGNED_URL_GENERATION_FAILED))
                .when(s3FaceClient).generateDownloadPreSignedUrl(eq(imageKey), eq(bucketName));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> faceManagementService.getFaceImageUrl(userId));

        // then
        assertEquals(ErrorCode.PRESIGNED_URL_GENERATION_FAILED, exception.getErrorCode());
        // faceRepository 호출 검증
        verify(faceRepository, times(1)).findFaceIdByUserId(userId);
        // S3 Presigned URL 생성이 호출되었는지 검증
        verify(s3FaceClient, times(1)).generateDownloadPreSignedUrl(imageKey, bucketName);
    }

    @Test
    @DisplayName("얼굴 이미지 삭제 - 성공!")
    void deleteFaceImage_Success() {
        // given
        Long userId = 1L;
        String imageKey = "images/faces/1/face.jpg";
        String rekognitionFaceId = "rekog-12345";
        Face face = Face.of(imageKey, rekognitionFaceId, userId);

        when(faceRepository.findFaceIdByUserId(userId)).thenReturn(face);
        doNothing().when(s3FaceClient).deleteFaceImage(imageKey, bucketName);
        doNothing().when(faceRepository).deleteByUserId(userId);
        doNothing().when(rekognitionApiClient).deleteFaceFromCollection(rekognitionFaceId);

        // when
        faceManagementService.deleteFaceImage(userId);

        // then
        verify(faceRepository, times(1)).findFaceIdByUserId(userId);
        verify(s3FaceClient, times(1)).deleteFaceImage(imageKey, bucketName);
        verify(faceRepository, times(1)).deleteByUserId(userId);
        verify(rekognitionApiClient, times(1)).deleteFaceFromCollection(rekognitionFaceId);
    }

    @Test
    @DisplayName("얼굴 이미지 삭제 실패 - FaceRepository 조회 실패 시 예외 발생")
    void deleteFaceImage_WhenFaceNotFound() {
        // given
        Long userId = 1L;

        // FaceRepository 에서 유저 얼굴 정보가 없을 경우 예외 발생하도록 설정
        doThrow(new CustomException(ErrorCode.FACE_NOT_FOUND))
                .when(faceRepository).findFaceIdByUserId(userId);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> faceManagementService.deleteFaceImage(userId));

        // then
        assertEquals(ErrorCode.FACE_NOT_FOUND, exception.getErrorCode());
        // FaceRepository 는 호출이 된다.
        verify(faceRepository, times(1)).findFaceIdByUserId(userId);
        // S3와 Rekognition API가 호출되지 않아야 함
        verifyNoInteractions(s3FaceClient, rekognitionApiClient);
    }

    @Test
    @DisplayName("얼굴 이미지 삭제 실패 - S3 이미지 삭제 실패 시 예외 발생")
    void deleteFaceImage_WhenS3DeletionFails() {
        // given
        Long userId = 1L;
        String imageKey = "images/faces/1/face.jpg";
        String rekognitionFaceId = "rekog-12345";
        Face face = Face.of(imageKey, rekognitionFaceId, userId);

        when(faceRepository.findFaceIdByUserId(userId)).thenReturn(face);

        // S3 이미지 삭제 실패 설정
        doThrow(new CustomException(ErrorCode.FILE_DELETE_FAILED))
                .when(s3FaceClient).deleteFaceImage(imageKey, bucketName);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> faceManagementService.deleteFaceImage(userId));

        // then
        assertEquals(ErrorCode.FILE_DELETE_FAILED, exception.getErrorCode());
        // S3 삭제 호출
        verify(s3FaceClient, times(1)).deleteFaceImage(imageKey, bucketName);
        // Rekognition 및 DB 삭제가 호출되지 않는다.
        verifyNoInteractions(rekognitionApiClient);
        verify(faceRepository, never()).deleteByUserId(userId);
    }

    @Test
    @DisplayName("얼굴 이미지 삭제 실패 - Rekognition 얼굴 삭제 실패 시 예외 발생")
    void deleteFaceImage_WhenRekognitionDeletionFails() {
        // given
        Long userId = 1L;
        String imageKey = "images/faces/1/face.jpg";
        String rekognitionFaceId = "rekog-12345";
        Face face = Face.of(imageKey, rekognitionFaceId, userId);

        when(faceRepository.findFaceIdByUserId(userId)).thenReturn(face);

        // S3 삭제 성공 설정
        doNothing().when(s3FaceClient).deleteFaceImage(imageKey, bucketName);

        // Rekognition 얼굴 삭제 실패 설정
        doThrow(new CustomException(ErrorCode.REKOGNITION_DELETE_FAILED))
                .when(rekognitionApiClient).deleteFaceFromCollection(rekognitionFaceId);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> faceManagementService.deleteFaceImage(userId));

        // then
        assertEquals(ErrorCode.REKOGNITION_DELETE_FAILED, exception.getErrorCode());
        // S3 삭제가 호출
        verify(s3FaceClient, times(1)).deleteFaceImage(imageKey, bucketName);
        // Rekognition 삭제 호출
        verify(rekognitionApiClient, times(1)).deleteFaceFromCollection(rekognitionFaceId);

        // DB 삭제는 호출되지 않음
        verify(faceRepository, never()).deleteByUserId(userId);
    }
}