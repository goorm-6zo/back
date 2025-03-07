package goorm.back.zo6.admin.application;

import goorm.back.zo6.admin.domain.IdExistenceDao;
import goorm.back.zo6.admin.domain.QRCodeGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QRCodeServiceImplTest {
    @Mock private IdExistenceDao conferenceIdExistenceDao;
    @Mock private IdExistenceDao sessionIdExistenceDao;
    @Mock private QRCodeGenerator qrCodeGenerator;
    @Mock private QRContentBuilder qrContentBuilder;
    @InjectMocks private QRCodeServiceImpl qrCodeServiceImpl;

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("QR 코드 생성 성공")
    void QRCode_Create_Success() {
        when(conferenceIdExistenceDao.exists(1L)).thenReturn(true);
        when(sessionIdExistenceDao.exists(1L)).thenReturn(true);
        when(qrContentBuilder.build(1L, 1L, "https://www.google.com")).thenReturn("test-content");
        when(qrCodeGenerator.generate("test-content")).thenReturn("test-qr-image");

        QRCodeResponse response = qrCodeServiceImpl.createQRCode(1L, 1L, "https://www.google.com");

        assertThat(response.qrImageBase64()).isEqualTo("test-qr-image");
    }
}
