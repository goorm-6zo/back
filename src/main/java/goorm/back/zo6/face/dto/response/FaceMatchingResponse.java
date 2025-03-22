package goorm.back.zo6.face.dto.response;

import lombok.Builder;

@Builder
public record FaceMatchingResponse(
        Long userId,
        float similarity
) {
    public static FaceMatchingResponse of(Long userId, float similarity){
        return FaceMatchingResponse.builder()
                .userId(userId)
                .similarity(similarity)
                .build();
    }
}
