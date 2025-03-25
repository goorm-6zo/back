package goorm.back.zo6.attend.presentation;

import goorm.back.zo6.attend.infrastructure.AttendRedisService;
import goorm.back.zo6.common.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "redis", description = "Redis API")
@RequestMapping("/api/v1/redis")
@RestController
@RequiredArgsConstructor
public class RedisController {
    private final AttendRedisService attendRedisService;

    @DeleteMapping
    @Operation(summary = "redis에 저장된 참석 정보 전부 삭제", description = "일관성 있는 테스트를 위한 redis key 전부 삭제 기능입니다. <br>" +
            "서버가 내려갔다 다시 실행되면 rdb 데이터는 날라가는데 redis는 그대로 인 상황에서 사용합니다.")
    public ResponseEntity<ResponseDto<String>> deleteAllRedisKey(){
        attendRedisService.deleteAllKeys();
        return ResponseEntity.ok(ResponseDto.of("redis key 전부 삭제 성공"));
    }
}
