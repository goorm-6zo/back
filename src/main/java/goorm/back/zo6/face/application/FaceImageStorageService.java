package goorm.back.zo6.face.application;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaceImageStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${amazon.aws.s3.bucket-name}")
    private String bucketName;

    // 얼굴 이미지 업로드를 위한 PreSigned URL 생성(프론트에서 업로드)
    public String generateUploadPreSignedUrl(Long userId){
        String fileName = getFileName(userId);
        URL uploadPreSignedUrl = createPresignedUrl(fileName, HttpMethod.PUT);
        return uploadPreSignedUrl.toString();
    }

    // 얼굴 이미지 다운로드 url 반환
    public String getFaceImageUrl(Long userId){
        String fileName = getFileName(userId);
        URL getPreSignedUrl = createPresignedUrl(fileName, HttpMethod.GET);
        return getPreSignedUrl.toString();
    }

    // 얼굴 이미지 다운로드 (바이트 배열 반환)
    public byte[] downloadFaceImage(Long userId) throws IOException {
        String fileName = getFileName(userId);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return s3Client.getObject(getObjectRequest).readAllBytes();
    }

    // 얼굴 이미지 삭제
    public void deleteFaceImage(Long userId) {
        String fileName = getFileName(userId);
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build());
    }

    private URL createPresignedUrl(String fileName, HttpMethod method) {
        return method == HttpMethod.PUT
                ? s3Presigner.presignPutObject(
                b -> b.putObjectRequest(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType("image/jpeg")
                        .build()
                ).signatureDuration(Duration.ofMinutes(50))
        ).url()
                : s3Presigner.presignGetObject(
                b -> b.getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build()
                ).signatureDuration(Duration.ofMinutes(50))
        ).url();
    }

    private String getFileName(Long userId) {
        return String.format("images/faces/%d/face.jpg",userId);
    }

}