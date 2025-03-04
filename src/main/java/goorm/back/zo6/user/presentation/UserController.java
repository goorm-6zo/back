package goorm.back.zo6.user.presentation;

import goorm.back.zo6.auth.util.CookieUtil;
import goorm.back.zo6.user.application.UserService;
import goorm.back.zo6.user.dto.request.LoginRequest;
import goorm.back.zo6.user.dto.request.SignUpRequest;
import goorm.back.zo6.user.dto.response.LoginResponse;
import goorm.back.zo6.user.dto.response.SignUpResponse;
import goorm.back.zo6.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "유저 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/no-token/{user-id}")
    public ResponseEntity<UserResponse> findById(@PathVariable("user-id")Long userId){
        return ResponseEntity.ok().body(userService.findById(userId));
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "유저를 등록합니다.")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
        SignUpResponse response = userService.signUp(request);
        return ResponseEntity.ok(response);
    }
}
