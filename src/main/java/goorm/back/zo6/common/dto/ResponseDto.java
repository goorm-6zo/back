package goorm.back.zo6.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseDto<T> {
    private final boolean status;
    private final T data;

    @Builder
    private ResponseDto(T data){
        this.status = true;
        this.data = data;
    }

    public static <T> ResponseDto<T> of(T data){
        return ResponseDto.<T>builder().data(data).build();
    }
}
