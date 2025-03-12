package goorm.back.zo6.qr.infrastructure;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import goorm.back.zo6.qr.domain.QRCodeGenerator;
import org.springframework.stereotype.Component;
import java.util.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Component
class QRCodeGeneratorImpl implements QRCodeGenerator {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 300;
    private static final String IMAGE_FORMAT = "png";

    @Override
    public String generate(String content) {
        return generate(content, null, null);
    }

    public String generate(String content, Long conferenceId, Long sessionId) {
        try {
            String formattedContent = formatUrl(content, conferenceId, sessionId);
            BitMatrix bitMatrix = createQRCodeMatrix(formattedContent);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            return encodeImageToBase64(image);
        } catch (Exception e) {
            throw new RuntimeException("QR 생성 실패", e);
        }
    }

    private String formatUrl(String content, Long conferenceId, Long sessionId) {
        StringBuilder urlBuilder = new StringBuilder(content);
        if (conferenceId != null || sessionId != null) {
            urlBuilder.append("?");
        }
        if (conferenceId != null) {
            urlBuilder.append("conferenceId=").append(conferenceId);
        }
        if (sessionId != null) {
            if (conferenceId != null) {
                urlBuilder.append("&");
            }
            urlBuilder.append("sectionId=").append(sessionId);
        }
        return urlBuilder.toString();
    }

    private BitMatrix createQRCodeMatrix(String content) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        return qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT);
    }

    private String encodeImageToBase64(BufferedImage image) throws Exception {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, IMAGE_FORMAT, byteArrayOutputStream);
            String base64Image = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            return "data:image/png;base64," + base64Image;
        }
    }
}
