package goorm.back.zo6.face.dto.response;

import lombok.Builder;

@Builder
public record FaceMatchingResponse(
        String userId,
        float similarity
) {
    public static FaceMatchingResponse of(String userId, float similarity){
        return FaceMatchingResponse.builder()
                .userId(userId)
                .similarity(similarity)
                .build();
    }
}
