package goorm.back.zo6.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest (
        @Schema(description = "이메일", example = "test@test.com")
        @NotBlank(message = "이메일을 입력해 주세요.")
        String email
){
}
