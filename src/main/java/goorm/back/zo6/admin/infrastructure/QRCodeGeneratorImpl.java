package goorm.back.zo6.admin.infrastructure;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import goorm.back.zo6.admin.domain.QRCodeGenerator;
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
        try {
            BitMatrix bitMatrix = createQRCodeMatrix(content);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            return encodeImageToBase64(image);
        } catch (Exception e) {
            throw new RuntimeException("QR 생성 실패", e);
        }
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
