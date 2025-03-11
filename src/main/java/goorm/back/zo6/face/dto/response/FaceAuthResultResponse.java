package goorm.back.zo6.face.dto.response;

import lombok.Builder;

@Builder
public record FaceAuthResultResponse(
        String userId,
        Float similarity,
        boolean result
) {
    public static FaceAuthResultResponse of(String userId, Float similarity){
        return FaceAuthResultResponse.builder()
                .userId(userId)
                .similarity(similarity)
                .result(true)
                .build();
    }
}