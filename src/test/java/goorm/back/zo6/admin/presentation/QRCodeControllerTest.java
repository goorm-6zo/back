package goorm.back.zo6.admin.presentation;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class QRCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    String testToken = createTestToken();

    @Test
    @DisplayName("QR 코드 생성 성공")
    void QRCode_Create_Success() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/admin/qr")
                                .cookie(new Cookie("Authorization", testToken))
                                .param("conferenceId", "1")
                                .param("sectionId", "1")
                                .param("url", "https://www.google.com")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    public static String createTestToken() {
        String secretKey = "qwbqwuiobhiqebfoizskldnf389y239rfhnoweuh2389h232f3";
        long now = System.currentTimeMillis();
        long validTime = 1000L * 60 * 30; // 30분

        return Jwts.builder()
                .setSubject("test-user")
                .claim("userId", 1L)
                .claim("email", "test@example.com")
                .claim("name", "테스트유저")
                .claim("role", "USER")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + validTime))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }
}
