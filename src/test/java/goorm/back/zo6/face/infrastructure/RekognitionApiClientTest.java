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
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.Face;
import software.amazon.awssdk.services.rekognition.model.FaceRecord;
import software.amazon.awssdk.services.rekognition.model.IndexFacesRequest;
import software.amazon.awssdk.services.rekognition.model.IndexFacesResponse;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RekognitionApiClientTest {
    @InjectMocks
    private RekognitionApiClient rekognitionApiClient;
    @Mock
    private RekognitionClient rekognitionClient;

    private String collectionId;

    @BeforeEach
    void setUp(){
        collectionId = "test-collection-name";
        // Value 값 직접 주입
        ReflectionTestUtils.setField(rekognitionApiClient, "collectionId", "test-collection-name");
    }

    @Test
    @DisplayName("Rekognition Collection 에 얼굴 등록 - 성공")
    void addFaceToCollection_Success() {
        // given
        Long userId = 1L;
        String imageKey = "images/faces/1/face.jpg";
        String bucketName = "test-bucket-name";
        String rekognitionFaceId = "rekognition-1234";
        Face face = Face.builder().faceId(rekognitionFaceId).build();
        FaceRecord faceRecord = FaceRecord.builder().face(face).build();
        IndexFacesResponse response = IndexFacesResponse.builder()
                .faceRecords(faceRecord)
                .build();

        // Rekognition API 응답
        when(rekognitionClient.indexFaces(any(IndexFacesRequest.class))).thenReturn(response);

        // when
        String result = rekognitionApiClient.addFaceToCollection(imageKey, userId, bucketName);

        // then
        assertEquals(rekognitionFaceId, result);
        verify(rekognitionClient, times(1)).indexFaces(any(IndexFacesRequest.class));
    }

    // Rekognition 이 얼굴을 감지하지 못한 경우 예외 발생 테스트
    @Test
    @DisplayName("Rekognition Collection 에 얼굴 등록 - 얼굴 등록 실패 시 예외 발생")
    void addFaceToCollection_WhenFaceUploadFails() {
        // given
        Long userId = 1L;
        String imageKey = "images/faces/1/face.jpg";
        String bucketName = "test-bucket-name";

        // 얼굴을 감지하지 못한 응답 설정
        IndexFacesResponse response = IndexFacesResponse.builder()
                .faceRecords(Collections.emptyList())
                .build();

        when(rekognitionClient.indexFaces(any(IndexFacesRequest.class))).thenReturn(response);

        // when & then
        // 예외 발생 검증
        assertThrows(CustomException.class, () -> rekognitionApiClient.addFaceToCollection(imageKey, userId, bucketName));

        // API 호출 확인
        verify(rekognitionClient, times(1)).indexFaces(any(IndexFacesRequest.class));
    }

    // Rekognition 이 얼굴을 감지하지 못한 경우 예외 발생 테스트
    @Test
    @DisplayName("Rekognition Collection 에 얼굴 등록 - Rekognition Collection Api 예외 발생")
    void addFaceToCollection_WhenApiFails() {
        // given
        Long userId = 1L;
        String imageKey = "images/faces/1/face.jpg";
        String bucketName = "test-bucket-name";

        doThrow(new CustomException(ErrorCode.REKOGNITION_API_FAILURE))
                .when(rekognitionClient).indexFaces(any(IndexFacesRequest.class));

        // when & then
        // 예외 발생 검증
        assertThrows(CustomException.class, () -> rekognitionApiClient.addFaceToCollection(imageKey, userId, bucketName));

        // API 호출 확인
        verify(rekognitionClient, times(1)).indexFaces(any(IndexFacesRequest.class));
    }

    @Test
    @DisplayName("")
    void authorizeUserFace_Success() {
    }

    @Test
    void deleteFaceFromCollection() {
    }

    @Test
    void createCollection() {
    }
}