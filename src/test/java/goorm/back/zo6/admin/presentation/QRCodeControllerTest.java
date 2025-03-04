package goorm.back.zo6.admin.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class QRCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("QR 코드 생성 성공")
    void QRCode_Create_Success() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/admin/qr")
                                .param("conferenceId", "1")
                                .param("sectionId", "1")
                                .param("url", "https://www.google.com")
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }
}
