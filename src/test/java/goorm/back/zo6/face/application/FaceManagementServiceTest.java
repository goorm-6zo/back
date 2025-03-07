package goorm.back.zo6.face.application;

import goorm.back.zo6.auth.application.AuthService;
import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.face.domain.FaceRepository;
import goorm.back.zo6.face.infrastructure.RekognitionApiClient;
import goorm.back.zo6.face.infrastructure.S3FaceClient;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FaceManagementServiceTest {

    @InjectMocks
    private FaceManagementService faceManagementService;
    @Mock
    private FaceRepository faceRepository;
    @Mock
    private  S3FaceClient s3FaceClient;
    @Mock
    private RekognitionApiClient rekognitionApiClient;
    @Mock
    private JwtUtil jwtUtil;
    private User testUser;

    @Test
    @DisplayName("얼굴 이미지 업로드 성공")
    void uploadUserFaceTest() {
        
    }

    @Test
    void getFaceImageUrl() {
    }

    @Test
    void deleteFaceImage() {
    }
}