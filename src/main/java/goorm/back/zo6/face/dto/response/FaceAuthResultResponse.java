package goorm.back.zo6.face.dto.response;

import lombok.Builder;

@Builder
public record FaceAuthResultResponse(
        Long userId,
        Float similarity
) {
    public static FaceAuthResultResponse of(Long userId, Float similarity){
        return FaceAuthResultResponse.builder()
                .userId(userId)
                .similarity(similarity)
                .build();
    }
}