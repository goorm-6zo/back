package goorm.back.zo6.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PhoneRequest(
        @Schema(description = "전화 번호", example = "01011112222")
        @NotBlank(message = "전화 번호를 입력해 주세요.")
        String phone
) {
}
