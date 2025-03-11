package goorm.back.zo6.face.infrastructure;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3FaceClientTest {

    @InjectMocks
    private S3FaceClient s3FaceClient;

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    private String bucketName;

    @BeforeEach
    void setUp(){
        bucketName = "test-bucket-name";
    }

    @Test
    @DisplayName("S3에 이미지 업로드 - 성공")
    void uploadFile_Success() {
        // given
        String fileName = "face.jpg";
        MultipartFile file = new MockMultipartFile(
                "test-image.jpg",
                "test-image.jpg",
                "image/jpeg",
                "test data".getBytes()
        );

        // S3 이미지 업로드 성공
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build()); // 빈 응답 반환


        // when & then
        assertDoesNotThrow(()-> s3FaceClient.uploadFile(file, fileName, bucketName));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("S3에 이미지 업로드 - S3 Api 업로드 실패")
    void uploadFile_WhenApiFails() {
        // given
        String fileName = "face.jpg";
        MultipartFile file = new MockMultipartFile(
                "test-image.jpg",
                "test-image.jpg",
                "image/jpeg",
                "test data".getBytes()
        );

        // S3 이미지 업로드 실패
        doThrow(new RuntimeException()).when(s3Client)
                .putObject(any(PutObjectRequest.class), any(RequestBody.class));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> s3FaceClient.uploadFile(file, fileName, bucketName));

        // then
        assertEquals(ErrorCode.FACE_UPLOAD_FAIL,exception.getErrorCode());
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }


    @Test
    @DisplayName("조회 PresignedUrl 생성 - 성공")
    void generateDownloadPreSignedUrl_Success() throws MalformedURLException {
        // given
        String imageKey = "images/faces/1/face.jpg";
        String testUrl = "https://s3.amazonaws.com/test-bucket/face-images/user123.jpg?AWSAccessKeyId=ACCESS_KEY&Expires=1640995200&Signature=abcd";
        URL mockPresignedUrl = new URL(testUrl);
        PresignedGetObjectRequest request = mock(PresignedGetObjectRequest.class);

        when(s3Presigner.presignGetObject(any(Consumer.class)))
                .thenReturn(request);
        when(request.url()).thenReturn(mockPresignedUrl);

        // when
        String getPresingedUrl = s3FaceClient.generateDownloadPreSignedUrl(imageKey,bucketName);

        // then
        assertEquals(getPresingedUrl, testUrl);
        verify(s3Presigner, times(1)).presignGetObject(any(Consumer.class));
    }

    @Test
    @DisplayName("조회 PresignedUrl 생성 - s3Presigner Api 에러 실패")
    void generateDownloadPreSignedUrl_WhenApiFails() {
        // given
        String imageKey = "images/faces/1/face.jpg";

        // void 메서드가 아니여서 when 으로 예외를 던짐
        when(s3Presigner.presignGetObject(any(Consumer.class))).thenThrow(new RuntimeException());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> s3FaceClient.generateDownloadPreSignedUrl(imageKey, bucketName));

        // then
        assertEquals(ErrorCode.PRESIGNED_URL_GENERATION_FAILED, exception.getErrorCode());
        verify(s3Presigner, times(1)).presignGetObject(any(Consumer.class));
    }

    @Test
    @DisplayName("S3 얼굴 이미지 삭제 - 성공")
    void deleteFaceImage_Success() {
        // given
        String imageKey = "images/faces/1/face.jpg";

        // 삭제 성공
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(DeleteObjectResponse.builder().build());

        // when & then
        assertDoesNotThrow(() -> s3FaceClient.deleteFaceImage(imageKey, bucketName));
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("S3 얼굴 이미지 삭제 - s3Client Api 에러 실패")
    void deleteFaceImage_WhenApiFails() {
        // given
        String imageKey = "images/faces/1/face.jpg";

        // 삭제 성공
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenThrow(new RuntimeException());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> s3FaceClient.deleteFaceImage(imageKey, bucketName));

        // then
        assertEquals(ErrorCode.FILE_DELETE_FAILED, exception.getErrorCode());
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }
}