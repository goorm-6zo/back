package goorm.back.zo6.face.application;

import goorm.back.zo6.auth.application.AuthService;
import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.face.domain.Face;
import goorm.back.zo6.face.domain.FaceRepository;
import goorm.back.zo6.face.dto.response.FaceResponse;
import goorm.back.zo6.face.infrastructure.RekognitionApiClient;
import goorm.back.zo6.face.infrastructure.S3FaceClient;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @DisplayName("얼굴 이미지 업로드 성공")
    void uploadUserFace_Success() {
        // given
        Long userId = 1L;
        String imageKey = "images/faces/1/face.jpg";
        String rekognitionFaceId = "rekog-12345";
        Face face = Face.of(imageKey, rekognitionFaceId, userId);
        MultipartFile faceImage = mock(MultipartFile.class);

        when(faceImage.getOriginalFilename()).thenReturn("test.jpg");
        // s3 저장 성공
        doNothing().when(s3FaceClient).uploadFile(faceImage, imageKey, bucketName);
        // collection 저장 성공
        when(rekognitionApiClient.addFaceToCollection(imageKey, userId, bucketName)).thenReturn(rekognitionFaceId);
        // Face 객체 저장
        when(faceRepository.save(any(Face.class))).thenReturn(face);

        // when
        FaceResponse response = faceManagementService.uploadUserFace(userId, faceImage);

        // then
        assertNotNull(response);
        assertEquals(rekognitionFaceId, response.rekognitionId());
        assertEquals(imageKey, response.imageKey());

        verify(s3FaceClient, times(1)).uploadFile(faceImage,imageKey,bucketName);
        verify(rekognitionApiClient, times(1)).addFaceToCollection(imageKey, userId, bucketName);
        verify(faceRepository, times(1)).save(any(Face.class));
    }

    @Test
    @DisplayName("얼굴 이미지 조회 presigned url 생성 성공!")
    void getFaceImageUrl() {
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
    void deleteFaceImage() {
        // given

        // when

        // then

    }
}