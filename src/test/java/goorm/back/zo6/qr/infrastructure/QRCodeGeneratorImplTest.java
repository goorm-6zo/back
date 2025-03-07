package goorm.back.zo6.qr.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
public class QRCodeGeneratorImplTest {

    @InjectMocks
    private QRCodeGeneratorImpl qrCodeGenerator;

    @Test
    @DisplayName("QR 코드 생성 성공")
    void generate_QRCode_Success() {
        String content = "https://www.google.com";

        String result = qrCodeGenerator.generate(content);

        assertThat(result).startsWith("data:image/png;base64,");
    }
}
