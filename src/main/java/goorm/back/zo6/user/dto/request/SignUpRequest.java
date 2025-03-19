package goorm.back.zo6.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @Schema(description = "이름", example = "홍길순")
        @NotBlank(message = "이름을 입력해 주세요.")
        String name,
        @Schema(description = "이메일", example = "test@test.com")
        @NotBlank(message = "이메일을 입력해 주세요.")
        String email,
        @Schema(description = "비밀번호", example = "12345")
        @NotBlank(message = "비밀번호를 입력해 주세요")
        String password,
        @Schema(description = "전화 번호", example = "01011112222")
        @NotBlank(message = "전화 번호를 입력해 주세요.")
        String phone
) {
}
