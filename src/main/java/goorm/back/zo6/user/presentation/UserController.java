package goorm.back.zo6.user.presentation;

import goorm.back.zo6.auth.domain.LoginUser;
import goorm.back.zo6.user.application.UserService;
import goorm.back.zo6.user.dto.request.SignUpRequest;
import goorm.back.zo6.user.dto.response.SignUpResponse;
import goorm.back.zo6.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "유저 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    @Operation(summary = "id 유저 조회", description = "유저 id로 유저를 조회합니다.")
    public ResponseEntity<UserResponse> findById(@PathVariable("userId") Long userId){
        return ResponseEntity.ok().body(userService.findById(userId));
    }

    @GetMapping
    @Operation(summary = "토큰 유저 조회", description = "유저 토큰으로 유저를 조회합니다.")
    public ResponseEntity<UserResponse> findByToken(@AuthenticationPrincipal LoginUser loginUser){
        String email = loginUser.user().getEmail();
        return ResponseEntity.ok().body(userService.findByToken(email));
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "유저를 등록합니다.")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok().body(userService.signUp(request));
    }

    @DeleteMapping
    @Operation(summary = "유저 탈퇴", description = "유저 토큰으로 유저를 논리 탈퇴합니다.")
    public ResponseEntity<String> delete(@AuthenticationPrincipal LoginUser loginUser) {
        String email = loginUser.user().getEmail();
        userService.delete(email);
        return ResponseEntity.ok().body("성공적으로 회원 탈퇴하였습니다.");
    }
}
