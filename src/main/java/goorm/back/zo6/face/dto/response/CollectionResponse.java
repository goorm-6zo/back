package goorm.back.zo6.face.dto.response;

import lombok.Builder;

@Builder
public record CollectionResponse(
        String collectionArn
) {
    public static CollectionResponse of(String collectionArn){
        return CollectionResponse.builder()
                .collectionArn(collectionArn)
                .build();
    }
}
