package goorm.back.zo6.user.presentation;

import goorm.back.zo6.user.application.UserService;
import goorm.back.zo6.user.dto.request.SignUpRequest;
import goorm.back.zo6.user.dto.response.SignUpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "Admin API")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "관리자 회원가입", description = "관리자를 등록합니다.")
    public ResponseEntity<SignUpResponse> adminSignUp(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok().body(userService.adminSignUp(request));
    }
}
