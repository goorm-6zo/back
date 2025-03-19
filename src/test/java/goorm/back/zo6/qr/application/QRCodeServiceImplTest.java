package goorm.back.zo6.qr.application;

import goorm.back.zo6.qr.domain.IdExistenceDao;
import goorm.back.zo6.qr.domain.QRCodeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QRCodeServiceImplTest {

    @Mock
    private QRValidator qrValidator;

    @Mock
    private QRCodeGenerator qrCodeGenerator;

    @Mock
    private QRContentBuilder qrContentBuilder;

    @InjectMocks
    private QRCodeServiceImpl qrCodeServiceImpl;

    @Test
    @DisplayName("QR 코드 생성 성공")
    void QRCode_Create_Success() {
        doNothing().when(qrValidator).validateSessionId(1L);
        doNothing().when(qrValidator).validateConferenceId(1L);
        when(qrContentBuilder.build(1L, 1L, "https://www.google.com")).thenReturn("test-content");
        when(qrCodeGenerator.generate("test-content")).thenReturn("test-qr-image");

        QRCodeResponse response = qrCodeServiceImpl.createQRCode(1L, 1L, "https://www.google.com");

        assertThat(response.qrImageBase64()).isEqualTo("test-qr-image");
    }
}
