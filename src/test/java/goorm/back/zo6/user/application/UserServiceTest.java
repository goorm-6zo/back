package goorm.back.zo6.user.application;

import goorm.back.zo6.auth.application.AuthService;
import goorm.back.zo6.auth.util.JwtUtil;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.user.domain.Password;
import goorm.back.zo6.user.domain.Role;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import goorm.back.zo6.user.dto.request.SignUpRequest;
import goorm.back.zo6.user.dto.response.SignUpResponse;
import goorm.back.zo6.user.dto.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    private User testUser;

    @BeforeEach
    void setUp(){
    }

    @Test
    @DisplayName("유저 ID로 조회 시, 정상적으로 UserResponse 를 반환한다.")
    void findById_Success() {
        // given
        Long userId = 1L;
        testUser = User.builder()
                .id(1L)
                .name("홍길순")
                .email("test@gmail.com")
                .phone("01011112222")
                .birthDate("2000-10-20")
                .password(Password.from(passwordEncoder.encode("1234")))
                .role(Role.of("USER"))
                .build();

        // findById()가 정상적으로 유저를 반환
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // when
        UserResponse response = userService.findById(userId);

        // then
        assertNotNull(response);
        assertEquals("test@gmail.com", response.email());
        assertEquals("홍길순", response.name());
        assertEquals("01011112222", response.phone());
        assertEquals("2000-10-20", response.birthDate());
        assertEquals("test@gmail.com", response.email());
        assertEquals(Role.USER, response.role());
        
        // Verify
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("회원가입 성공 시, SignUpResponse 를 반환한다.")
    void signUp_Success() {
        // given
        SignUpRequest request = new SignUpRequest("홍길동","newuser@gmail.com", "1234",  "2000-01-01","01033334444");

        // 기존 이메일이 존재하지 않음
        when(userRepository.findByEmailAndIsDeleted(request.email(), false)).thenReturn(Optional.empty());
        //  비밀번호 암호화
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        //  새로운 유저 저장 후 반환
        User newUser = User.singUpUser(request.email(), request.name(), "encodedPassword", request.phone(), request.birth_date(), Role.of("USER"));
        ReflectionTestUtils.setField(newUser,"id",1L);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // when
        SignUpResponse response = userService.signUp(request);

        // then
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("newuser@gmail.com", response.email());
        assertEquals("홍길동", response.name());
        assertEquals("01033334444", response.phone());
        assertEquals(Role.USER, response.role());

        // Verify
        verify(userRepository, times(1)).findByEmailAndIsDeleted(request.email(), false);
        verify(passwordEncoder, times(1)).encode(request.password());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 회원가입 시, 예외 발생")
    void signUp_Fail_UserAlreadyExists() {
        // given
        SignUpRequest request = new SignUpRequest("홍길동","exist@gmail.com", "1234",  "2000-01-01","01033334444");

        // 이미 존재하는 유저
        when(userRepository.findByEmailAndIsDeleted(request.email(), false)).thenReturn(Optional.of(User.builder().build()));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> userService.signUp(request));

        // then
        assertEquals(ErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());

        // Verify
        verify(userRepository, times(1)).findByEmailAndIsDeleted(request.email(), false);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}