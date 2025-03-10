package goorm.back.zo6.face.infrastructure;

import goorm.back.zo6.face.dto.response.FaceMatchingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.nio.ByteBuffer;

@Component
@RequiredArgsConstructor
@Log4j2
public class RekognitionApiClient {

    private final RekognitionClient rekognitionClient;

    @Value("${amazon.aws.rekognition.collection-id}")
    private String collectionId;

    //얼굴을 Rekognition Collection 에 등록하고 Rekognition Face ID 반환
    public String addFaceToCollection(String imageKey, Long userId, String bucketName) {
        String externalImageId = String.valueOf(userId);
        IndexFacesRequest request = IndexFacesRequest.builder()
                .collectionId(collectionId)
                .image(Image.builder()
                        .s3Object(S3Object.builder()
                                .bucket(bucketName)
                                .name(imageKey)
                                .build())
                        .build())
                .externalImageId(externalImageId)
                .build();

        IndexFacesResponse response = rekognitionClient.indexFaces(request);

        // collection 에 얼굴 데이터 저장 결과 확인
        if (!response.faceRecords().isEmpty()) {
            return response.faceRecords().get(0).face().faceId(); // Rekognition Face ID 반환
        }

        return null;
    }

    // 업로드된 이미지와 Collection 내의 이미지들과 얼굴 비교 후 가장 유사한 사용자 id 반환
    public FaceMatchingResponse recognizeUserFace(ByteBuffer imageBytes) {
        SearchFacesByImageRequest request = SearchFacesByImageRequest.builder()
                .collectionId(collectionId)
                .image(Image.builder().bytes(SdkBytes.fromByteBuffer(imageBytes)).build())
                .maxFaces(1)
                .faceMatchThreshold(85f)  // 85% 이상 일치하는 경우만 인증 성공
                .build();

        SearchFacesByImageResponse response = rekognitionClient.searchFacesByImage(request);
        // 일치하는 얼굴 정보가 있었는지 확인
        if (!response.faceMatches().isEmpty()) {
            FaceMatch match = response.faceMatches().get(0);
            String userId = match.face().externalImageId();
            float similarity = match.similarity();
            log.info("유저 {} 얼굴정보 확인, 유사도 : {}", userId, similarity);
            return FaceMatchingResponse.of(userId, similarity);
        }

        return null;
    }

    //Rekognition Collection 에서 얼굴 삭제
    public void deleteFaceFromCollection(String rekognitionFaceId) {
        DeleteFacesRequest deleteFacesRequest = DeleteFacesRequest.builder()
                .collectionId(collectionId)
                .faceIds(rekognitionFaceId)
                .build();

        rekognitionClient.deleteFaces(deleteFacesRequest);
        log.info("얼굴 이미지 정보 삭제 완료! faceId: {}", rekognitionFaceId);
    }

    // Rekognition Collection 생성 (최초 1회 실행)
    public void createCollection() {
        CreateCollectionRequest request = CreateCollectionRequest.builder()
                .collectionId(collectionId)
                .build();

        CreateCollectionResponse response = rekognitionClient.createCollection(request);
        log.info("Rekognition Collection 생성 완료! ARN: {}", response.collectionArn());
    }


}
