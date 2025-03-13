package goorm.back.zo6.face.application;

import goorm.back.zo6.common.event.Events;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.face.dto.response.CollectionResponse;
import goorm.back.zo6.face.dto.response.FaceAuthResultResponse;
import goorm.back.zo6.face.dto.response.FaceMatchingResponse;
import goorm.back.zo6.face.infrastructure.RekognitionApiClient;
import goorm.back.zo6.face.infrastructure.event.AttendanceEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RekognitionServiceTest {
    @InjectMocks
    private RekognitionService rekognitionService;
    @Mock
    private RekognitionApiClient rekognitionApiClient;

    @Test
    @DisplayName("얼굴 인식 - 성공")
    void authenticationByUserFace_Success() throws IOException {
        // given
        String userId = "1";
        float similarity = 92.5f;
        MultipartFile uploadedFile = mock(MultipartFile.class);
        Long conferenceId = 1L;
        Long sessionId = 1L;
        MockedStatic<Events> mockEvents = mockStatic(Events.class);

        // 정상적인 파일 변환 설정
        mockEvents.when(()-> Events.raise(any(AttendanceEvent.class))).thenAnswer(invocation -> null);
        when(uploadedFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        ByteBuffer imageBytes = ByteBuffer.wrap(new byte[]{1, 2, 3});

        // Rekognition API가 정상적으로 응답을 반환하도록 설정
        FaceMatchingResponse matchingResponse = FaceMatchingResponse.of(userId, similarity);
        when(rekognitionApiClient.authorizeUserFace(imageBytes)).thenReturn(matchingResponse);

        // when
        FaceAuthResultResponse result = rekognitionService.authenticationByUserFace(conferenceId, sessionId, uploadedFile);

        // then
        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(similarity, result.similarity());
        verify(rekognitionApiClient, times(1)).authorizeUserFace(imageBytes);
    }

    @Test
    @DisplayName("얼굴 인식 - 매치되는 얼굴이 없어서 인증 실패")
    void authenticationByUserFace_WhenMatchFails() throws IOException {
        // given
        MultipartFile uploadedFile = mock(MultipartFile.class);
        Long conferenceId = 1L;
        Long sessionId = 1L;

        // 정상적인 파일 변환 설정
        when(uploadedFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        ByteBuffer imageBytes = ByteBuffer.wrap(new byte[]{1, 2, 3});

        // Rekognition API 얼굴 매칭 실패
        doThrow(new CustomException(ErrorCode.REKOGNITION_NO_MATCH_FOUND))
                .when(rekognitionApiClient).authorizeUserFace(imageBytes);

        // when
        CustomException exception = assertThrows(CustomException.class, () -> rekognitionService.authenticationByUserFace(conferenceId, sessionId, uploadedFile));


        // then
        assertEquals(ErrorCode.REKOGNITION_NO_MATCH_FOUND, exception.getErrorCode());
        verify(rekognitionApiClient, times(1)).authorizeUserFace(imageBytes);
    }
    // 파일 변환 중 IOException 발생 시 예외 확인
    @Test
    @DisplayName("얼굴 인식 - 파일 변환 중 IOException 발생 시 예외 발생")
    void authenticationByUserFace_WhenIOException() throws IOException {
        // given
        MultipartFile uploadedFile = mock(MultipartFile.class);
        Long conferenceId = 1L;
        Long sessionId = 1L;

        // 파일 변환 중 IOException 발생하도록 설정
        when(uploadedFile.getBytes()).thenThrow(new IOException());

        // when
        CustomException exception = assertThrows(CustomException.class, () -> rekognitionService.authenticationByUserFace(conferenceId, sessionId, uploadedFile));

        // then
        assertEquals(ErrorCode.FILE_CONVERSION_EXCEPTION, exception.getErrorCode());
        verifyNoInteractions(rekognitionApiClient);
    }

    @Test
    @DisplayName("rekognition collection 생성 - 성공")
    void createCollection_Success() {
        // given
        // collection 생성
        String expectedArn = "arn:aws:rekognition:us-east-1:123456789012:collection/test-collection";

        when(rekognitionApiClient.createCollection()).thenReturn(expectedArn);

        // when
        CollectionResponse response = rekognitionService.createCollection();

        // then
        assertNotNull(response);
        assertEquals(expectedArn,response.collectionArn());
        verify(rekognitionApiClient, times(1)).createCollection();
    }

    @Test
    @DisplayName("rekognition collection 생성 - collection 생성 실패")
    void createCollection_Fails() {
        // given
        // collection 생성시 에러 발생
        doThrow(new CustomException(ErrorCode.REKOGNITION_CREATE_COLLECTION_FAIL))
                .when(rekognitionApiClient).createCollection();

        // when
        CustomException exception = assertThrows(CustomException.class, () -> rekognitionService.createCollection());

        //then
        assertEquals(ErrorCode.REKOGNITION_CREATE_COLLECTION_FAIL, exception.getErrorCode());
        verify(rekognitionApiClient, times(1)).createCollection();
    }
}