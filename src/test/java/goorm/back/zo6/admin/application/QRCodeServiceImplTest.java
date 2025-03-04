package goorm.back.zo6.admin.application;

import goorm.back.zo6.admin.domain.QRCodeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QRCodeServiceImplTest {
    @Mock
    private QRCodeGenerator qrCodeGenerator;

    @Mock
    private QRContentBuilder qrContentBuilder;

    @InjectMocks
    private QRCodeServiceImpl qrCodeServiceImpl;

    @Test
    @DisplayName("QR 코드 생성 성공")
    void QRCode_Create_Success() {
        Long conferenceId = 1L;
        Long sectionId = 1L;
        String url = "https://www.google.com";
        String qrContent = "conferenceId=1&sectionId=1&url=https://www.google.com";
        String qrImageBase64 = "base64ImageData";

        when(qrContentBuilder.build(conferenceId, sectionId, url)).thenReturn(qrContent);
        when(qrCodeGenerator.generate(qrContent)).thenReturn(qrImageBase64);

        QRCodeResponse response = qrCodeServiceImpl.createQRCode(conferenceId, sectionId, url);

        assertThat(response.qrImageBase64()).isEqualTo(qrImageBase64);
    }
}
