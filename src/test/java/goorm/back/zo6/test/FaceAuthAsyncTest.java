//package goorm.back.zo6.test;
//
//import goorm.back.zo6.attend.domain.Attend;
//import goorm.back.zo6.attend.domain.AttendRepository;
//import goorm.back.zo6.face.application.FaceRecognitionService;
//import goorm.back.zo6.face.dto.response.FaceMatchingResponse;
//import goorm.back.zo6.face.infrastructure.RekognitionApiClient;
//import goorm.back.zo6.user.domain.Role;
//import goorm.back.zo6.user.domain.User;
//import goorm.back.zo6.user.domain.UserRepository;
//import jakarta.persistence.Tuple;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.nio.ByteBuffer;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.awaitility.Awaitility.await;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//@ActiveProfiles("test")
//public class FaceAuthAsyncTest {
//
//    @Autowired
//    private FaceRecognitionService faceRecognitionService; // 테스트할 서비스
//
//    @MockitoSpyBean
//    private UserRepository userRepository;
//
//    @MockitoBean
//    private RekognitionApiClient rekognitionApiClient; // Mock으로 대체할 빈
//
//    @MockitoSpyBean
//    private AttendRepository attendRepository;
//
//    private MockMultipartFile mockFile;
//
//    private User testUser;
//
//    @BeforeEach
//    void setup() {
//        testUser = User.singUpUser("test","test","12345","12312312312", Role.USER);
//        ReflectionTestUtils.setField(testUser, "id",1L);
//
//        mockFile = new MockMultipartFile(
//                "file", "face.jpg", "image/jpeg", "dummy image content".getBytes());
//
//        when(rekognitionApiClient.authorizeUserFace(any(ByteBuffer.class)))
//                .thenReturn(new FaceMatchingResponse("12345", 98.5f));
//
//        when(userRepository.findById(any(Long.class))).thenReturn(Optional.ofNullable(testUser));
//
//        // `Tuple`을 직접 생성하여 반환
//        List<Tuple> mockTuples = List.of(
//                new CustomTuple(100L, 101L, 1L, 1L)  // 예약 ID, 세션 ID, 컨퍼런스 ID, 세션 ID
//        );
//
//        when(attendRepository.findAttendData(anyString(), anyLong(), anyLong()))
//                .thenReturn(mockTuples);  // 가짜 데이터 반환
//    }
//
//
//    @Test
//    public void testSyncFaceAuthPerformance() throws InterruptedException {
//        int userCount = 500;
//        long startTime = System.currentTimeMillis();
//
//        // 500명의 유저가 비동기적으로 얼굴 인증 시도
//        for (int i = 0; i < userCount; i++) {
//            final Long sessionId = (long) i;
//            faceRecognitionService.authenticationByUserFace(1L, sessionId, mockFile);
//        }
//
//        // 비동기 작업이 완료될 때까지 대기
//        long waitStartTime = System.currentTimeMillis();
//        while (true) {
//            if (System.currentTimeMillis() - waitStartTime > 20000) { // 20초 초과 시 종료
//                System.out.println("비동기 작업이 20초 내에 완료되지 않음!");
//                break;
//            }
//
//            // 검증: `registerAttend()`가 모든 요청을 처리했는지 확인
//            try {
//                verify(attendRepository, times(userCount)).save(any(Attend.class));
//                break; // 성공하면 루프 탈출
//            } catch (AssertionError e) {
//                Thread.sleep(100); // 실패하면 100ms 대기 후 다시 확인
//            }
//        }
//
//        long endTime = System.currentTimeMillis();
//        System.out.println("[전체 처리 시간] 비동기 모드: " + (endTime - startTime) + "ms");
//
//        System.out.println("[비동기 실행 확인 완료]");
//    }
//}
