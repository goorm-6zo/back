package goorm.back.zo6.face.dto.response;

import goorm.back.zo6.face.domain.Face;
import lombok.Builder;

@Builder
public record FaceResponse(
        Long id,
        Long userId,
        String rekognitionId
) {
    public static FaceResponse from(Face face){
        return FaceResponse.builder()
                .id(face.getId())
                .userId(face.getUserId())
                .rekognitionId(face.getRekognitionFaceId())
                .build();
    }
}
