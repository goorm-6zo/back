package goorm.back.zo6.face.infrastructure;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.face.dto.response.FaceMatchingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

import java.nio.ByteBuffer;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Log4j2
public class RekognitionApiClient {

    private final RekognitionClient rekognitionClient;

    @Value("${amazon.aws.rekognition.collection-id}")
    private String collectionId;

    //얼굴을 Rekognition Collection 에 등록하고 Rekognition Face ID 반환
    public String addFaceToCollection(String imageKey, Long userId, String bucketName) {
        try {
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

            // AWS Rekognition API 호출
            IndexFacesResponse response = rekognitionClient.indexFaces(request);

            // 얼굴 정보가 업로드 되지 않은 경우
            if (response.faceRecords().isEmpty()) {
                log.info("collection 얼굴 업로드 실패.");
                throw new CustomException(ErrorCode.FACE_UPLOAD_FAIL);
            }

            // 정상적인 경우 Rekognition Face ID 반환
            return response.faceRecords().get(0).face().faceId();

        }catch (CustomException e){
            throw e;
        }
        catch (Exception e) {
            // 얼굴 업로드 api 호출 시 에러가 발생
            log.info("collection 에 얼굴 업로드 중 api 서버 에러.");
            throw new CustomException(ErrorCode.REKOGNITION_API_FAILURE);
        }
    }

    // 업로드된 이미지와 Collection 내의 이미지들과 얼굴 비교 후 가장 유사한 사용자 id 반환
    public FaceMatchingResponse authorizeUserFace(ByteBuffer imageBytes) {
        try {
            SearchFacesByImageRequest request = SearchFacesByImageRequest.builder()
                    .collectionId(collectionId)
                    .image(Image.builder().bytes(SdkBytes.fromByteBuffer(imageBytes)).build())
                    .maxFaces(1)
                    .faceMatchThreshold(85f)  // 85% 이상 일치하는 경우만 인증 성공
                    .build();

            // AWS Rekognition API 호출
            SearchFacesByImageResponse response = rekognitionClient.searchFacesByImage(request);

            // 얼굴 매칭이 되지 않은 경우
            if (response.faceMatches().isEmpty()) {
                log.info("얼굴 인증 실패 - 일치하는 얼굴 정보가 없음.");
                throw new CustomException(ErrorCode.REKOGNITION_NO_MATCH_FOUND);
            }

            FaceMatch match = response.faceMatches().get(0);
            String userId = match.face().externalImageId();
            float similarity = match.similarity();

            return FaceMatchingResponse.of(userId, similarity);

        }catch (CustomException e){
            throw e;
        }
        catch (Exception e){
            // 얼굴 매칭 api 호출 시 에러가 발생
            log.info("얼굴 매칭 중 api 서버 에러.");
            throw new CustomException(ErrorCode.REKOGNITION_API_FAILURE);
        }
    }

    //Rekognition Collection 에서 얼굴 삭제
    public void deleteFaceFromCollection(String rekognitionFaceId) {
        try{
            DeleteFacesRequest deleteFacesRequest = DeleteFacesRequest.builder()
                    .collectionId(collectionId)
                    .faceIds(rekognitionFaceId)
                    .build();

            rekognitionClient.deleteFaces(deleteFacesRequest);
        }catch (Exception e){
            log.info("얼굴 이미지 정보 삭제 실패! faceId: {}", rekognitionFaceId);
            throw  new CustomException(ErrorCode.REKOGNITION_DELETE_FAILED);
        }
    }

    // Rekognition Collection 생성 (최초 1회 실행)
    public String createCollection() {
        try{
            CreateCollectionRequest request = CreateCollectionRequest.builder()
                    .collectionId(collectionId)
                    .build();

            CreateCollectionResponse response = rekognitionClient.createCollection(request);
            log.info("Rekognition Collection 생성 완료! ARN: {}", response.collectionArn());
            return response.collectionArn();
        }catch (Exception e){
            log.info("collection 생성 중, api 서버 에러.");
            throw new CustomException(ErrorCode.REKOGNITION_CREATE_COLLECTION_FAIL);
        }
    }
}
