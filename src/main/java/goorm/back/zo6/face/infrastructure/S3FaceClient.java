package goorm.back.zo6.face.infrastructure;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class S3FaceClient {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    // 서버에서 직접 S3에 이미지 업로드
    public void uploadFile(MultipartFile file, String fileName, String bucketName) {
        try {
            // 파일을 S3에 업로드
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromBytes(file.getBytes())
            );
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FACE_UPLOAD_FAIL);
        }
    }

    // 얼굴 이미지 다운로드 URL 반환
    public String generateDownloadPreSignedUrl(String imageKey,String bucketName) {
        URL getPreSignedUrl = createPresignedUrl(imageKey, bucketName);
        return getPreSignedUrl.toString();
    }

    // 얼굴 이미지 삭제
    public void deleteFaceImage(String imageKey, String bucketName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(imageKey)
                .build());
    }

    // PreSigned URL 생성
    private URL createPresignedUrl(String imageKey, String bucketName) {
        return s3Presigner.presignGetObject(
                b -> b.getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(imageKey)
                        .build()
                ).signatureDuration(Duration.ofMinutes(50)))
                .url();
    }
}
