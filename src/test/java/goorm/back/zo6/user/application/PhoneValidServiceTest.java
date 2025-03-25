package goorm.back.zo6.user.application;

import net.nurigo.sdk.message.service.DefaultMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class PhoneValidServiceTest {
    @InjectMocks
    private PhoneValidService phoneValidService;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private DefaultMessageService defaultMessageService;

    @Test
    @DisplayName("전화번호 인증을 성공합니다.")
    public void phoneValidTest(){
        //given
        String phone = "01012345678";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(phone)).thenReturn("123456");

        //when
        boolean isValid = phoneValidService.validPhone(phone,"123456");

        //then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("전화번호 인증을 실패합니다.")
    public void phoneValidFailTest(){
        //given
        String phone = "01012345678";
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(phone)).thenReturn("123456");

        //when
        boolean isValid = phoneValidService.validPhone(phone,"654321");

        //then
        assertFalse(isValid);
    }
}
