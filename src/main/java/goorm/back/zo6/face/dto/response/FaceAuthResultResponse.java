package goorm.back.zo6.face.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL) // null 값인 필드는 JSON에서 제외
public record FaceAuthResultResponse(
        String userId,
        Float similarity,
        boolean result
) {
    // 인증 성공 (userId, similarity 포함)
    public FaceAuthResultResponse(String userId, float similarity) {
        this(userId, similarity, true);
    }

    // 인증 실패 (result만 포함)
    public FaceAuthResultResponse() {
        this(null, null, false);
    }
}