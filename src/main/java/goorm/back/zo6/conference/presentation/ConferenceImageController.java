package goorm.back.zo6.conference.presentation;

import goorm.back.zo6.conference.infrastructure.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/conferences/image")
public class ConferenceImageController {

    private final S3FileService s3FileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadConferenceImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            String fileKey = s3FileService.uploadFile(file, "conference/images/");
            return ResponseEntity.ok(fileKey);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 에러" + e.getMessage());
        }
    }
}
