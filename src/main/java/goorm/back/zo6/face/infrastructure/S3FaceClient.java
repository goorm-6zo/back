package goorm.back.zo6.face.infrastructure;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
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
        } catch (Exception e) {
            log.info("s3에 이미지 업로드를 실패하였습니다.");
            throw new CustomException(ErrorCode.FACE_UPLOAD_FAIL);
        }
    }

    // 얼굴 이미지 다운로드 URL 반환
    public String generateDownloadPreSignedUrl(String imageKey,String bucketName) {
        try{
            URL getPreSignedUrl = createPresignedUrl(imageKey, bucketName);
            return getPreSignedUrl.toString();
        }catch (Exception e){
            log.info("s3 이미지 조회 PresingedUrl 생성에 실패하였습니다.");
            throw new CustomException(ErrorCode.PRESIGNED_URL_GENERATION_FAILED);
        }
    }

    // 얼굴 이미지 삭제
    public void deleteFaceImage(String imageKey, String bucketName) {
        try{
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(imageKey)
                    .build());
        }catch (Exception e){
            log.info("s3에 얼굴 이미지 삭제에 실패하였습니다.");
            throw new CustomException(ErrorCode.FILE_DELETE_FAILED);
        }
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
